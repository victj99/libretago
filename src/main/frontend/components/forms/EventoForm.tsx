import { useForm } from "@vaadin/hilla-react-form"
import { Button, DateTimePicker, Notification, RadioButton, RadioGroup, TextArea, TextField } from "@vaadin/react-components"
import EventoDTOModel from "Frontend/generated/com/utp/libretago/classes/dto/EventoDTOModel"
import { AdministrarEventoEndpoint, EventoUsuarioEndpoint, GrupoEndpoint } from "Frontend/generated/endpoints"
import { useAuth } from "Frontend/security/auth"
import handleError from "Frontend/views/_ErrorHandler"
import { useEffect, useState } from "react"
import { ComboBoxFilterMultiple } from "../common/ComboBoxFilter"
import { useConfirm } from "../common/ConfirmDialog"
import LoadingOverlay from "../LoadingOverlay"

interface EventoFormProps {
  eventoId?: number
  onCerrar: (eventoId?: number) => void
}

export function EventoForm(props: EventoFormProps) {
  const [puedeEvaluar, setPuedeEvaluar] = useState(false)
  const [loading, setLoading] = useState(false)
  const [gruposDefecto, setGruposDefecto] = useState<any[] | undefined>()

  const confirmDialog = useConfirm()
  const { hasAccess } = useAuth()

  const { model, field, submit, read, clear } = useForm(EventoDTOModel, {
    onSubmit: async (e) => {
      const confirm = await confirmDialog({
        header: 'Registrar',
        text: '¿Desea registrar el evento?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        let id
        if (hasAccess({ rolesAllowed: ['PROFESOR'] }) && !hasAccess({ rolesAllowed: ['COLEGIO'] })) {
          // Si es solo profesor, usa el endpoint de usuario
          id = await EventoUsuarioEndpoint.crearEvento(e)
        } else {
          id = await (props.eventoId ?
            AdministrarEventoEndpoint.editarEvento(props.eventoId, e) :
            AdministrarEventoEndpoint.crearEvento(e))
        }

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
    if (!props.eventoId) {
      return
    }

    setLoading(true)
    AdministrarEventoEndpoint.obtenerEvento(props.eventoId).then((evento) => {
      if (evento && evento.id) {
        read(evento)
        setPuedeEvaluar(evento.estado === 'P' && hasAccess({ rolesAllowed: ['COLEGIO'] }))

        if (evento.grupos) setGruposDefecto(evento.grupos)
      }
    }).finally(() => setLoading(false))
  }, [props.eventoId])

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
      
      <DateTimePicker
        label="Fecha del Evento"
        required
        {...field(model.fechaEvento)}
      />

      <ComboBoxFilterMultiple
        label="Grupo"
        required
        readonly={!!props.eventoId}
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
