import { EndpointError } from "@vaadin/hilla-frontend"
import { useForm } from "@vaadin/hilla-react-form"
import { useState } from "react"
import { Button, Checkbox, ComboBox, Notification, TextField } from "@vaadin/react-components"

import UsuarioInstitucionDTOModel from "Frontend/generated/com/utp/libretago/classes/dto/UsuarioInstitucionDTOModel"
import { InstitucionEducativaEndpoint, UsuarioInstitucionEndpoint } from "Frontend/generated/endpoints"
import { useEffect } from "react"
import { useConfirm } from "../common/ConfirmDialog"
import LoadingOverlay from "../LoadingOverlay"
import handleError from "Frontend/views/_ErrorHandler"
import { useComboBoxDataProvider } from "@vaadin/hilla-react-crud"

export interface Props {
  usuarioId?: number
  onCerrar: (ieId?: number) => void
}

export function UsuarioColegioForm(props: Props) {
  const confirmDialog = useConfirm()
  const [loading, setLoading] = useState(false)
  const [nombreIe, setNombreIe] = useState('')

  const comboProvider = useComboBoxDataProvider((page, nombre) => InstitucionEducativaEndpoint.listarInstituciones(page, nombre))

  const { model, field, submit, read, clear } = useForm(UsuarioInstitucionDTOModel, {
    onSubmit: async (e) => {
      const confirm = await confirmDialog({
        header: 'Registrar Usuario',
        text: '¿Desea registrar el usuario?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        const id = await (props.usuarioId ?
          UsuarioInstitucionEndpoint.editarUsuario(props.usuarioId, e) :
          UsuarioInstitucionEndpoint.crearUsuario(e))

        Notification.show('Registrado con éxito', { position: 'bottom-end', theme: 'success' })

        props.onCerrar(id)
      } catch (error) {
        handleError(error)
      }
    }
  })

  useEffect(() => {
    clear()
    if (props.usuarioId) {
      setLoading(true)
      UsuarioInstitucionEndpoint.obtenerUsuario(props.usuarioId).then(resp => {
        read(resp)
        setNombreIe(resp?.nombreInstitucion || '')
      }).finally(() => setLoading(false))
    }
  }, [props.usuarioId])

  return <>
    <LoadingOverlay mostrar={loading} />

    <div className={"grid sm:grid-cols-2 md:grid-cols-3 gap-2 items-center"}>
      <TextField
        className="md:col-span-2"
        label='Nombre de usuario'
        readonly={props.usuarioId ? true : false}
        maxlength={255}
        {...field(model.nombreUsuario)}
      />

      <TextField
        className=""
        label='Nombres y apellidos'
        maxlength={50}
        {...field(model.nombreCompleto)}
      />

      <TextField
        className="md:col-span-2"
        label='Correo electrónico'
        maxlength={255}
        {...field(model.email)}
      />

      <TextField
        className=""
        label='Teléfono'
        maxlength={20}
        {...field(model.telefono)}
      />

      <ComboBox
        className={`md:col-span-2 ${!props.usuarioId || 'hidden'}`}
        label='Institución educativa'
        dataProvider={comboProvider}
        {...field(model.institucionEducativaId)}
      />

      {props.usuarioId && <div className="md:col-span-2">
        Institución educativa:&nbsp;
        <strong>{nombreIe}</strong>
      </div>
      }

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