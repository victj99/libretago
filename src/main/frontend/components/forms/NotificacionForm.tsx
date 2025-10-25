import { useForm } from "@vaadin/hilla-react-form"
import { Button, Notification, RadioButton, RadioGroup, TextArea, TextField } from "@vaadin/react-components"
import NotificacionDTOModel from "Frontend/generated/com/utp/libretago/classes/dto/NotificacionDTOModel"
import { AdministrarNotificacionEndpoint, GrupoEndpoint } from "Frontend/generated/endpoints"
import { useAuth } from "Frontend/security/auth"
import handleError from "Frontend/views/_ErrorHandler"
import { useEffect, useState } from "react"
import { ComboBoxFilterMultiple } from "../common/ComboBoxFilter"
import { useConfirm } from "../common/ConfirmDialog"
import LoadingOverlay from "../LoadingOverlay"

interface NotificacionFormProps {
  notificacionId?: number
  onCerrar: (notificacionId?: number) => void
}

export function NotificacionForm(props: NotificacionFormProps) {
  const [puedeEvaluar, setPuedeEvaluar] = useState(false)
  const [loading, setLoading] = useState(false)
  const [gruposDefecto, setGruposDefecto] = useState<any[] | undefined>()

  const confirmDialog = useConfirm()
  const { hasAccess } = useAuth()

  const { model, field, submit, read, clear } = useForm(NotificacionDTOModel, {
    onSubmit: async (e) => {
      const confirm = await confirmDialog({
        header: 'Registrar',
        text: '¿Desea registrar la notificación?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        const id = await (props.notificacionId ?
          AdministrarNotificacionEndpoint.editarNotificacion(props.notificacionId, e) :
          AdministrarNotificacionEndpoint.crearNotificacion(e))

        Notification.show('Registrado con éxito', { position: 'bottom-end', theme: 'success' })

        props.onCerrar(id)
        limpiar()
      } catch (error) {
        handleError(error)
      }
    }
  })

  function limpiar() {
    clear()
    setPuedeEvaluar(false)
  }

  useEffect(() => {
    limpiar()
    if (!props.notificacionId) {
      return
    }

    setLoading(true)
    AdministrarNotificacionEndpoint.obtenerNotificacion(props.notificacionId).then((notificacion) => {
      if (notificacion && notificacion.id) {
        read(notificacion)
        setPuedeEvaluar(notificacion.estado === 'P' && hasAccess({ rolesAllowed: ['COLEGIO'] }))

        if (notificacion.grupos) setGruposDefecto(notificacion.grupos)
      }
    }).finally(() => setLoading(false))
  }, [props.notificacionId])

  return <>
    <LoadingOverlay mostrar={loading} />
    <div className="flex flex-col gap-s p-m">
      <TextField
        label="Título"
        required
        {...field(model.titulo)}
      />
      <TextArea
        label="Detalle"
        required
        {...field(model.detalle)}
      />

      <ComboBoxFilterMultiple
        label="Grupo"
        required
        readonly={!!props.notificacionId}
        // @ts-ignore
        defaultItems={gruposDefecto}
        fetcher={GrupoEndpoint.listarGruposPorNombre}
        fieldModel={model.grupos}
      />

      {puedeEvaluar && (

        <RadioGroup
          label="¿Que desea hacer?"
          theme="horizontal"
          {...field(model.estado)}
        >
          <RadioButton value="A" label="Aprobar" />
          <RadioButton value="R" label="Rechazar" />
        </RadioGroup>
      )}

      <div className="flex flex-row gap-4">
        <Button theme="primary" onClick={submit}>Guardar</Button>

        <Button onClick={() => props.onCerrar()}>Cancelar</Button>
      </div>
    </div>
  </>
}

