import { EndpointError } from "@vaadin/hilla-frontend"
import { useForm } from "@vaadin/hilla-react-form"
import { useState } from "react"
import { Button, Notification, TextField, Checkbox } from "@vaadin/react-components"
import { useEffect } from "react"
import LoadingOverlay from "../LoadingOverlay"
import InstitucionEducativaModel from "Frontend/generated/com/utp/libretago/entity/InstitucionEducativaModel"
import { InstitucionEducativaEndpoint } from "Frontend/generated/endpoints"
import { useConfirm } from "../common/ConfirmDialog"

export interface Props {
  ieId?: number
  onCerrar: (ieId?: number) => void
}

export function InstitucionEducativaForm(props: Props) {
  const confirmDialog = useConfirm()
  const [loading, setLoading] = useState(false)
  const { model, field, submit, read } = useForm(InstitucionEducativaModel, {
    onSubmit: async (e) => {
      const confirm = await confirmDialog({
        header: 'Registrar institución',
        text: '¿Desea registrar la institución?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        const id = await (props.ieId ?
          InstitucionEducativaEndpoint.editarInstitucion(props.ieId, e) :
          InstitucionEducativaEndpoint.crearInstitucion(e))

        Notification.show('Registrado con éxito', {
          position: 'bottom-end',
          theme: 'success',
        })

        props.onCerrar(id)
      } catch (error) {
        if (error instanceof EndpointError) {
          Notification.show(error.message, {
            position: 'bottom-end', theme: 'error',
          })

          return
        }

        Notification.show('Error al registrar', {
          position: 'bottom-end', theme: 'error',
        })
      }
    }
  })

  useEffect(() => {
    if (props.ieId) {
      setLoading(true)
      InstitucionEducativaEndpoint.obtenerInstitucion(props.ieId).then(resp => {
        read(resp)
      }).finally(() => setLoading(false))
    }
  }, [props.ieId])

  return <>
    <LoadingOverlay mostrar={loading} />

    <div className={"grid sm:grid-cols-2 md:grid-cols-3 gap-2"}>
      <TextField
        className="md:col-span-2"
        label='Nombre'
        maxlength={255}
        {...field(model.nombre)}
      />

      <TextField
        className=""
        label='Código Ugel'
        maxlength={50}
        {...field(model.codigoUgel)}
      />

      <TextField
        className="md:col-span-2"
        label='Dirección'
        maxlength={255}
        {...field(model.direccion)}
      />

      <TextField
        className=""
        label='Teléfono'
        maxlength={20}
        {...field(model.telefono)}
      />

      {props.ieId && <Checkbox label='Activo' {...field(model.activo)} />}

    </div>
    <br />
    <div className="flex flex-row gap-4">
      <Button
        theme="primary"
        onClick={submit}
      >
        Registrar
      </Button>

      <Button onClick={() => props.onCerrar()}>Cancelar</Button>
    </div>
  </>
}