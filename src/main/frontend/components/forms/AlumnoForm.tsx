import { useForm } from "@vaadin/hilla-react-form"
import { useState } from "react"
import { Button, Checkbox, Notification, TextField } from "@vaadin/react-components"

import AlumnoDTOModel from "Frontend/generated/com/utp/libretago/classes/dto/AlumnoDTOModel"
import { AlumnoEndpoint } from "Frontend/generated/endpoints"
import handleError from "Frontend/views/_ErrorHandler"
import { useEffect } from "react"
import { useConfirm } from "../common/ConfirmDialog"
import LoadingOverlay from "../LoadingOverlay"

export interface Props {
  alumnoId?: number
  onCerrar: (ieId?: number) => void
}

export function AlumnoForm(props: Props) {
  const confirmDialog = useConfirm()
  const [loading, setLoading] = useState(false)

  const { model, field, submit, read } = useForm(AlumnoDTOModel, {
    onSubmit: async (e) => {
      const confirm = await confirmDialog({
        header: 'Registrar Alumno',
        text: '¿Desea registrar el alumno?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        const id = await (props.alumnoId ?
          AlumnoEndpoint.editarAlumno(props.alumnoId, e) :
          AlumnoEndpoint.crearAlumno(e))

        Notification.show('Registrado con éxito', { position: 'bottom-end', theme: 'success' })

        props.onCerrar(id)
      } catch (error) {
        handleError(error)
      }
    }
  })

  useEffect(() => {
    if (props.alumnoId) {
      setLoading(true)
      AlumnoEndpoint.obtenerAlumno(props.alumnoId).then(resp => {
        read(resp)

      }).finally(() => setLoading(false))
    }
  }, [props.alumnoId])

  return <>
    <LoadingOverlay mostrar={loading} />

    <div className={"grid sm:grid-cols-2 md:grid-cols-2 gap-2 items-center"}>
      <TextField
        className=""
        label='Nombres'
        maxlength={255}
        {...field(model.nombres)}
      />

      <TextField
        className=""
        label='Apellidos'
        maxlength={255}
        {...field(model.apellidos)}
      />

      <TextField
        className=""
        label='Código de alumno'
        maxlength={50}
        {...field(model.codigoAlumno)}
      />

      <TextField
        className=""
        label='Teléfono'
        maxlength={20}
        {...field(model.telefono)}
      />

      <TextField
        className="md:col-span-2"
        label='Correo electrónico'
        maxlength={255}
        {...field(model.email)}
      />

      <strong className="col-span-2">Datos del apoderado</strong>

      <TextField
        className=""
        label='DNI o CE del apoderado'
        maxlength={9}
        {...field(model.dniCeApoderado)}
      />

      <TextField
        className=""
        label='Nombres y apellidos del apoderado'
        maxlength={50}
        {...field(model.nombreCompletoApoderado)}
      />

      {props.alumnoId && <Checkbox label='Activo' {...field(model.activo)} />}

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