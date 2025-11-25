import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { Button, Dialog, Grid, GridColumn, Icon, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { EventoForm } from 'Frontend/components/forms/EventoForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import EventoDTO from 'Frontend/generated/com/utp/libretago/classes/dto/EventoDTO'
import FiltroEvento from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroEvento'
import FiltroEventoModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroEventoModel'
import { AdministrarEventoEndpoint } from 'Frontend/generated/endpoints'
import { useAuth } from 'Frontend/security/auth'
import { useRef, useState } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'

export const config: ViewConfig = {
  title: 'Administrar Eventos',
  menu: {
    title: 'Eventos',
  },
  rolesAllowed: ['COLEGIO']
}

type FiltrosProps = {
  onBuscar: (filtros: FiltroEvento) => void
  onNuevo: () => void
}

export function FiltroEventoForm(props: FiltrosProps) {

  const { model, field, submit } = useForm(FiltroEventoModel, {
    onSubmit: async (e) => props.onBuscar(e)
  })

  return <>
    <div className='flex flex-wrap gap-1'>
      <TextField
        className='flex-1'
        aria-label="Título del evento"
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

export default function AdministrarEventosView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)
  const { hasAccess } = useAuth()

  const isColegio = hasAccess({ rolesAllowed: ['COLEGIO'] })
  const isProfesor = hasAccess({ rolesAllowed: ['PROFESOR'] })

  const filtros = useRef<FiltroEvento>({})

  const dataProvider = useGridDataProvider(async (page) => AdministrarEventoEndpoint.buscarEventosPorFiltros(filtros.current, page), [])

  function accionesTabla(data: { item: EventoDTO }) {
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

  function onCerrarDialog(eventoId?: number) {
    setDialogOpened(false)
    setEditId(undefined)
    if (eventoId) dataProvider.refresh()
  }

  async function onInactivarDialog(eventoId: number) {
    const confirmar = await confirmDialog({
      header: 'Desactivar', text: '¿Desea desactivar el evento?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await AdministrarEventoEndpoint.inactivarById(eventoId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Administrar eventos" />

      <FiltroEventoForm
        onBuscar={(val) => {
          filtros.current = val
          dataProvider.refresh()
        }}
        onNuevo={() => setDialogOpened(true)}
      />

      <Grid dataProvider={dataProvider}>
        <GridColumn path="titulo" header='Título' autoWidth />
        <GridColumn
          path="fechaEvento"
          header='Fecha Evento'
          autoWidth
          renderer={({ item }) => <>{item.fechaEvento?.substring(0, 16).replace('T', ' ')}</>}
        />

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
        headerTitle='Registrar evento'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <EventoForm
          eventoId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}
