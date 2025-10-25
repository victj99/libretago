import { useForm } from "@vaadin/hilla-react-form"
import { Button, Checkbox, Notification, TextField } from "@vaadin/react-components"
import { useState } from "react"

import UsuarioDTOModel from "Frontend/generated/com/utp/libretago/classes/dto/UsuarioDTOModel"
import { UsuarioProfesorEndpoint } from "Frontend/generated/endpoints"
import handleError from "Frontend/views/_ErrorHandler"
import { useEffect } from "react"
import { useConfirm } from "../common/ConfirmDialog"
import LoadingOverlay from "../LoadingOverlay"

export interface Props {
  usuarioId?: number
  onCerrar: (ieId?: number) => void
}

export function UsuarioProfesorForm(props: Props) {
  const confirmDialog = useConfirm()
  const [loading, setLoading] = useState(false)

  const { model, field, submit, read } = useForm(UsuarioDTOModel, {
    onSubmit: async (e) => {
      const confirm = await confirmDialog({
        header: 'Registrar Usuario',
        text: '¿Desea registrar el usuario?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        const id = await (props.usuarioId ?
          UsuarioProfesorEndpoint.editarUsuario(props.usuarioId, e) :
          UsuarioProfesorEndpoint.crearUsuario(e))

        Notification.show('Registrado con éxito', { position: 'bottom-end', theme: 'success' })

        props.onCerrar(id)
      } catch (error) {
        handleError(error)
      }
    }
  })

  useEffect(() => {
    if (props.usuarioId) {
      setLoading(true)
      UsuarioProfesorEndpoint.obtenerUsuario(props.usuarioId).then(resp => {
        read(resp)
      }).finally(() => setLoading(false))
    }
  }, [props.usuarioId])

  return <>
    <LoadingOverlay mostrar={loading} />

    <div className={"grid sm:grid-cols-2 gap-2 items-center"}>
      <TextField
        label='N° de identificación'
        readonly={props.usuarioId ? true : false}
        maxlength={9}
        {...field(model.nombreUsuario)}
      />

      <TextField
        label='Nombres y apellidos'
        maxlength={50}
        {...field(model.nombreCompleto)}
      />

      <TextField
        label='Correo electrónico'
        maxlength={255}
        {...field(model.email)}
      />

      <TextField
        label='Teléfono'
        maxlength={20}
        {...field(model.telefono)}
      />

      {props.usuarioId && <Checkbox label='Activo' {...field(model.activo)} />}

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