import { useForm, useFormArrayPart } from "@vaadin/hilla-react-form"
import { Button, Grid, GridColumn, Notification, TextField } from "@vaadin/react-components"
import GrupoDTOModel from "Frontend/generated/com/utp/libretago/classes/dto/GrupoDTOModel"
import { AlumnoEndpoint, GrupoEndpoint, UsuarioProfesorEndpoint } from "Frontend/generated/endpoints"
import handleError from "Frontend/views/_ErrorHandler"
import { useEffect, useState } from "react"
import { MdDelete } from "react-icons/md"
import { ComboBoxFilter } from "../common/ComboBoxFilter"
import { useConfirm } from "../common/ConfirmDialog"

export type GrupoFormProps = {
  grupoId?: number
  onCerrar: (id?: number) => void
}

export function GrupoForm(props: GrupoFormProps) {
  const [nombreProfesor, setNombreProfesor] = useState<string | undefined>()
  const [codigosAlumno, setCodigosAlumno] = useState('')
  const confirmDialog = useConfirm()

  const { model, field, submit, read, clear } = useForm(GrupoDTOModel, {
    onSubmit: async (grupo) => {

      const confirm = await confirmDialog({
        header: 'Registrar',
        text: '¿Desea registrar el grupo?',
        cancelable: true,
      })
      if (!confirm) return

      try {
        const id = await (props.grupoId ?
          GrupoEndpoint.editarGrupo(props.grupoId, grupo) :
          GrupoEndpoint.crearGrupo(grupo))

        Notification.show('Registrado con éxito', { position: 'bottom-end', theme: 'success' })

        props.onCerrar(id)
      } catch (error) {
        handleError(error)
      }
    }
  })
  const { value: itemsAlumnos, setValue, errors } = useFormArrayPart(model.alumnosNuevos)
  const { setValue: setValueEliminados, value: itemsAlumnosEliminados } = useFormArrayPart(model.alumnosEliminadosIds)

  async function agregarAlumnos() {
    const codigosArr = codigosAlumno.split(' ')
    const listaAlumnos = await AlumnoEndpoint.listarAlumnosPorCodigo(codigosArr)

    if (listaAlumnos.length > 0) {
      // Quitamos de la lista alumnos los que tengan un id que ya existe en itemsAlumnos
      const idsActuales = new Set(itemsAlumnos?.map(a => a.id))
      const nuevosAlumnos = listaAlumnos.filter(a => a.id && !idsActuales.has(a.id))

      setValue([...(itemsAlumnos || []), ...nuevosAlumnos])

      // Validamos si hay alumnos que no se encontraron
      const codigosObtenidos = listaAlumnos.map(item => item.codigoAlumno)
      const codigosNoEncontrados = codigosArr.filter(item => !codigosObtenidos.includes(item))

      if (codigosNoEncontrados.length > 0) {
        Notification.show('Los siguientes códigos de alumno no se encontraron: ' + codigosNoEncontrados.join(', '), { position: 'bottom-end', theme: 'warning' })
      }

      setCodigosAlumno('')
    }
  }

  useEffect(() => {
    setNombreProfesor('')
    if (!props.grupoId) {
      clear()
      return
    }

    GrupoEndpoint.obtenerGrupo(props.grupoId).then(async (grupo) => {
      if (grupo && grupo.id) {
        read(grupo)
        // Listamos los alumnos
        const listaAlumnos = await GrupoEndpoint.listarAlumnosPorGrupoId(grupo.id!)
        setValue(listaAlumnos)

        setNombreProfesor(grupo.nombreProfesor)
      }
    })
  }, [props.grupoId])

  return (
    <div className="flex flex-col gap-s p-m">
      <TextField
        required
        className="flex-1"
        aria-label="Nombre del grupo"
        label="Nombre"
        maxlength={255}
        {...field(model.nombre)}
      />

      <ComboBoxFilter
        className="md:col-span-2"
        label='Profesor'
        defaultLabel={nombreProfesor}
        fieldModel={model.usuarioProfesorId}
        fetcher={UsuarioProfesorEndpoint.listarUsuarios}
      />

      <div className="flex flex-row items-end gap-m">
        <TextField
          className="grow"
          label='Codigos de alumno'
          placeholder="Puede ingresar varios codigos de alumno separados por un espacio"
          value={codigosAlumno}
          onChange={(e) => setCodigosAlumno(e.target.value)}
        />

        <Button onClick={agregarAlumnos}>Agregar</Button>
      </div>

      <Grid items={itemsAlumnos}>
        <GridColumn path="codigoAlumno" />
        <GridColumn path="nombres" />
        <GridColumn path="apellidos" />

        <GridColumn
          header="Quitar"
          renderer={(data) => {
            return <Button
              theme="secondary error small icon"
              aria-label="Quitar alumno de la lista"
              onClick={() => {
                setValue(itemsAlumnos!.filter((item) => item.id !== data.item.id))
                if (props.grupoId) {
                  const idsSet = new Set(itemsAlumnosEliminados)
                  idsSet.add(data.item.id)
                  setValueEliminados([...idsSet])
                }
              }}
            >
              <MdDelete />
            </Button>
          }}
        />

      </Grid>
      {errors?.map(el => <span>{el.message}</span>)}

      <div className="flex gap-m">
        <Button theme="primary" onClick={submit}>
          Guardar
        </Button>
        <Button onClick={() => props.onCerrar()}>Cancelar</Button>
      </div>
    </div>
  )
}