import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { useState } from 'react'
import { Button, Dialog, Grid, GridColumn, Icon, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { AlumnoForm } from 'Frontend/components/forms/AlumnoForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import AlumnoDTO from 'Frontend/generated/com/utp/libretago/classes/dto/AlumnoDTO'
import FiltroAlumno from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroAlumno'
import FiltroAlumnoModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroAlumnoModel'
import { AlumnoEndpoint } from 'Frontend/generated/endpoints'
import { useRef } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'
import { NavLink, useLocation } from 'react-router'

export const config: ViewConfig = {
  title: 'Alumnos',
  menu: {
    title: 'Alumnos',
  },
  rolesAllowed: ['COLEGIO']
}

type FiltrosProps = {
  onBuscar: (filtros: FiltroAlumno) => void
  onNuevo: () => void
}

export function FiltroAlumnoForm(props: FiltrosProps) {
  const location = useLocation()

  const { model, field, submit } = useForm(FiltroAlumnoModel, {
    onSubmit: async (e) => props.onBuscar(e)
  })

  return <>
    <div className='flex flex-wrap gap-1'>
      <TextField
        className='flex-1'
        aria-label="Nombres del alumno"
        label="Nombres"
        maxlength={255}
        {...field(model.nombres)}
      />

      <TextField
        className='flex-1'
        aria-label="Apellidos del alumno"
        label="Apellidos"
        maxlength={255}
        {...field(model.apellidos)}
      />

      <TextField
        className='flex-1'
        aria-label="Código del alumno"
        label="Código de alumno"
        maxlength={255}
        {...field(model.codigoAlumno)}
      />
    </div>

    <div className='flex gap-2'>
      <Button onClick={submit} theme="primary">
        <Icon icon="vaadin:search" slot={'prefix'} />
        Buscar
      </Button>

      <Button onClick={props.onNuevo}>
        Nuevo
      </Button>

      <NavLink to='./registro-masivo'>
        <Button>Registro masivo</Button>
      </NavLink>
    </div>
  </>
}

export default function AlumnoView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)

  const filtros = useRef<FiltroAlumno>({})

  const dataProvider = useGridDataProvider(async (page) => AlumnoEndpoint.buscarPorFiltros(filtros.current, page), [])

  function accionesTabla(data: { item: AlumnoDTO }) {
    return <div className='flex flex-row gap-2'>
      <Button
        theme="secondary small icon"
        aria-label="Modificar registro"
        onClick={() => {
          setEditId(data.item.id!)
          setDialogOpened(true)
        }}>
        <MdEdit />
      </Button>

      {data.item.activo === true && <Button
        theme="secondary error small icon"
        aria-label="Desactivar registro"
        onClick={() => onInactivarDialog(data.item.id!)}
      >
        <MdDelete />
      </Button>}
    </div>
  }

  function onCerrarDialog(ieId?: number) {
    setDialogOpened(false)
    setEditId(undefined)
    if (ieId) dataProvider.refresh()
  }

  async function onInactivarDialog(ieId: number) {
    const confirmar = await confirmDialog({
      header: 'Desactivar', text: '¿Desea desactivar el alumno?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await AlumnoEndpoint.inactivarAlumno(ieId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Listado de alumnos" />

      <FiltroAlumnoForm
        onBuscar={(val) => {
          filtros.current = val
          dataProvider.refresh()
        }}
        onNuevo={() => setDialogOpened(true)}
      />

      <Grid dataProvider={dataProvider}>
        <GridColumn path="nombres" />
        <GridColumn path="apellidos" />
        <GridColumn path="codigoAlumno" />
        <GridColumn path="dniCeApoderado" header='DNI/Ce Apoderado' autoWidth />
        <GridColumn path="nombreCompletoApoderado" header='Apoderado' />
        <GridColumn path="telefono" />
        <GridColumn path="activo" renderer={({ item }) => <>{item.activo ? 'Sí' : 'No'}</>} />
        <GridColumn header="Acciones" renderer={accionesTabla} />
      </Grid>

      <Dialog
        opened={dialogOpened}
        noCloseOnOutsideClick
        headerTitle='Registrar alumno'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <AlumnoForm
          alumnoId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}
