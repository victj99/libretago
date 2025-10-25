import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { useState } from 'react'
import { Button, Dialog, Grid, GridColumn, GridSortColumn, Icon, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { NotificacionForm } from 'Frontend/components/forms/NotificacionForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import NotificacionDTO from 'Frontend/generated/com/utp/libretago/classes/dto/NotificacionDTO'
import FiltroNotificacion from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroNotificacion'
import FiltroNotificacionModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroNotificacionModel'
import { AdministrarNotificacionEndpoint } from 'Frontend/generated/endpoints'
import { useAuth } from 'Frontend/security/auth'
import { useRef } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'

export const config: ViewConfig = {
  title: 'Notificaciones',
  menu: {
    title: 'Notificaciones',
  },
  rolesAllowed: ['COLEGIO']
}

type FiltrosProps = {
  onBuscar: (filtros: FiltroNotificacion) => void
  onNuevo: () => void
}

export function FiltroNotificacionForm(props: FiltrosProps) {

  const { model, field, submit } = useForm(FiltroNotificacionModel, {
    onSubmit: async (e) => props.onBuscar(e)
  })

  return <>
    <div className='flex flex-wrap gap-1'>
      <TextField
        className='flex-1'
        aria-label="Título de la notificación"
        label="Título"
        maxlength={255}
        {...field(model.titulo)}
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

export default function AdministrarNotificacionesView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)
  const { hasAccess } = useAuth()

  const isColegio = hasAccess({ rolesAllowed: ['COLEGIO'] })
  const isProfesor = hasAccess({ rolesAllowed: ['PROFESOR'] })

  const filtros = useRef<FiltroNotificacion>({})

  const dataProvider = useGridDataProvider(async (page) => AdministrarNotificacionEndpoint.buscarNotificacionesPorFiltros(filtros.current, page), [])

  function accionesTabla(data: { item: NotificacionDTO }) {
    const isPendiente = data.item.estado === 'P'

    // PROFESOR no puede editar si está aprobado
    const puedeEditar = isColegio || (isProfesor && isPendiente)

    return <div className='flex flex-row gap-2'>
      {puedeEditar && <Button
        theme="secondary small icon"
        aria-label="Modificar registro"
        onClick={() => {
          setEditId(data.item.id!)
          setDialogOpened(true)
        }}>
        <MdEdit />
      </Button>}

      {data.item.activo === true && <Button
        theme="secondary error small icon"
        aria-label="Desactivar registro"
        onClick={() => onInactivarDialog(data.item.id!)}
      >
        <MdDelete />
      </Button>}
    </div>
  }

  function onCerrarDialog(notificacionId?: number) {
    setDialogOpened(false)
    setEditId(undefined)
    if (notificacionId) dataProvider.refresh()
  }

  async function onInactivarDialog(notificacionId: number) {
    const confirmar = await confirmDialog({
      header: 'Desactivar', text: '¿Desea desactivar la notificación?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await AdministrarNotificacionEndpoint.inactivarById(notificacionId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Administrar notificaciones" />

      <FiltroNotificacionForm
        onBuscar={(val) => {
          filtros.current = val
          dataProvider.refresh()
        }}
        onNuevo={() => setDialogOpened(true)}
      />

      <Grid dataProvider={dataProvider}>
        <GridColumn path="titulo" header='Título' />
        <GridColumn path="grupoNombre" header='Grupo' />

        {isColegio && <GridColumn path="usuarioCreadorNombre" header='Creado por' />}

        <GridColumn
          path="estado"
          header='Estado'
          renderer={({ item }) => {
            const estadoMap: Record<string, string> = {
              'P': 'Pendiente',
              'A': 'Aprobado',
              'R': 'Rechazado'
            }
            return <>{estadoMap[item.estado!] || item.estado}</>
          }}
        />
        <GridColumn path="activo" renderer={({ item }) => <>{item.activo ? 'Sí' : 'No'}</>} />
        <GridColumn header="Acciones" renderer={accionesTabla} />
      </Grid>

      <Dialog
        opened={dialogOpened}
        noCloseOnOutsideClick
        headerTitle='Registrar notificación'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <NotificacionForm
          notificacionId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}