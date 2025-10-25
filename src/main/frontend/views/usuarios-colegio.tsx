import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { useState } from 'react'
import { Button, Dialog, Grid, GridColumn, Icon, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { UsuarioColegioForm } from 'Frontend/components/forms/UsuarioColegioForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import UsuarioInstitucionDTO from 'Frontend/generated/com/utp/libretago/classes/dto/UsuarioInstitucionDTO'
import FiltroUsuario from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroUsuario'
import FiltroUsuarioModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroUsuarioModel'
import { UsuarioInstitucionEndpoint } from 'Frontend/generated/endpoints'
import { useRef } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'

export const config: ViewConfig = {
  title: 'Usuarios',
  menu: {
    title: 'Usuarios',
  },
  rolesAllowed: ['ADMIN']
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
    </div>
  </>
}

export default function UsuarioColegioView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)

  const filtros = useRef<FiltroUsuario>({})

  const dataProvider = useGridDataProvider(async (page) => UsuarioInstitucionEndpoint.buscarPorFiltros(filtros.current, page), [])

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

  function onCerrarDialog(ieId?: number) {
    setDialogOpened(false)
    setEditId(undefined)
    if (ieId) dataProvider.refresh()
  }

  async function onInactivarDialog(ieId: number) {
    const confirmar = await confirmDialog({
      header: 'Desactivar', text: '¿Desea desactivar el usuario?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await UsuarioInstitucionEndpoint.inactivarUsuario(ieId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Usuarios de instituciones" />

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
        <GridColumn path="nombreInstitucion" header='Institución educativa' />
        <GridColumn path="activo" renderer={({ item }) => <>{item.activo ? 'Sí' : 'No'}</>} />
        <GridColumn header="Acciones" renderer={accionesTabla} />
      </Grid>

      <Dialog
        opened={dialogOpened}
        noCloseOnOutsideClick
        headerTitle='Registrar usuario'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <UsuarioColegioForm
          usuarioId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}
