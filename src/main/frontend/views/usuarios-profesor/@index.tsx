import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { useState } from 'react'
import { Button, Dialog, Grid, GridColumn, Icon, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { UsuarioProfesorForm } from 'Frontend/components/forms/UsuarioProfesorForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import UsuarioInstitucionDTO from 'Frontend/generated/com/utp/libretago/classes/dto/UsuarioInstitucionDTO'
import FiltroUsuario from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroUsuario'
import FiltroUsuarioModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroUsuarioModel'
import { UsuarioProfesorEndpoint } from 'Frontend/generated/endpoints'
import { useRef } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'
import { NavLink } from 'react-router'

export const config: ViewConfig = {
  title: 'Usuarios',
  menu: {
    title: 'Usuarios',
  },
  rolesAllowed: ['COLEGIO']
}

type FiltrosProps = {
  onBuscar: (filtros: FiltroUsuario) => void
  onNuevo: () => void
}

export function FiltroUsuarioForm(props: FiltrosProps) {

  const { model, field, submit } = useForm(FiltroUsuarioModel, {
    onSubmit: async (e) => props.onBuscar(e)
  })

  return <>
    <div className='flex flex-wrap gap-1'>
      <TextField
        className='flex-1'
        aria-label="Nombre de usuario con el que inicia sesión"
        label="Nombre de usuario"
        maxlength={255}
        {...field(model.nombreUsuario)}
      />
      <TextField
        className='flex-2'
        aria-label="Nombres y apellidos del usuario"
        label="Nombres y apellidos"
        maxlength={255}
        {...field(model.nombreCompleto)}
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

export default function UsuarioProfesorView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)

  const filtros = useRef<FiltroUsuario>({})

  const dataProvider = useGridDataProvider(async (page) => UsuarioProfesorEndpoint.buscarPorFiltros(filtros.current, page), [])

  function accionesTabla(data: { item: UsuarioInstitucionDTO }) {
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

  function onCerrarDialog(usuarioId?: number) {
    setEditId(undefined)
    setDialogOpened(false)
    if (usuarioId) dataProvider.refresh()
  }

  async function onInactivarDialog(ieId: number) {
    const confirmar = await confirmDialog({
      header: 'Desactivar', text: '¿Desea desactivar el usuario?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await UsuarioProfesorEndpoint.inactivarUsuario(ieId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Usuarios de la institución educativa" />

      <FiltroUsuarioForm
        onBuscar={(val) => {
          filtros.current = val
          dataProvider.refresh()
        }}
        onNuevo={() => setDialogOpened(true)}
      />

      <Grid dataProvider={dataProvider}>
        <GridColumn path="nombreUsuario" header='Nombre de usuario' />
        <GridColumn path="nombreCompleto" header='Nombres y apellidos' />
        <GridColumn path="email" />
        <GridColumn path="telefono" />
        <GridColumn path="activo" renderer={({ item }) => <>{item.activo ? 'Sí' : 'No'}</>} />
        <GridColumn header="Acciones" renderer={accionesTabla} />
      </Grid>

      <Dialog
        opened={dialogOpened}
        noCloseOnOutsideClick
        headerTitle='Registrar usuario'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <UsuarioProfesorForm
          usuarioId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}
