import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { useState } from 'react'
import { Button, Dialog, Grid, GridColumn, Icon, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { GrupoForm } from 'Frontend/components/forms/GrupoForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import GrupoDTO from 'Frontend/generated/com/utp/libretago/classes/dto/GrupoDTO'
import FiltroGrupo from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroGrupo'
import FiltroGrupoModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroGrupoModel'
import { GrupoEndpoint } from 'Frontend/generated/endpoints'
import { useRef } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'

export const config: ViewConfig = {
  title: 'Grupos',
  menu: {
    title: 'Grupos',
  },
  rolesAllowed: ['COLEGIO']
}

type FiltrosProps = {
  onBuscar: (filtros: FiltroGrupo) => void
  onNuevo: () => void
}

export function FiltroGrupoForm(props: FiltrosProps) {
  const { model, field, submit } = useForm(FiltroGrupoModel, {
    onSubmit: async (e) => props.onBuscar(e)
  })

  return <>
    <div className='flex flex-wrap gap-1'>
      <TextField
        className='flex-1'
        aria-label="Nombre del grupo"
        label="Nombre"
        maxlength={255}
        {...field(model.nombre)}
      />

      {/* TODO: Agregar campos adicionales según las necesidades */}
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

export default function GruposView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)

  const filtros = useRef<FiltroGrupo>({})

  const dataProvider = useGridDataProvider(async (page) => GrupoEndpoint.buscarPorFiltros(filtros.current, page), [])

  function accionesTabla(data: { item: GrupoDTO }) {
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

  function onCerrarDialog(grupoId?: number) {
    setEditId(undefined)
    setDialogOpened(false)
    if (grupoId) dataProvider.refresh()
  }

  async function onInactivarDialog(grupoId: number) {
    const confirmar = await confirmDialog({
      header: 'Desactivar', text: '¿Desea desactivar el grupo?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await GrupoEndpoint.inactivarGrupo(grupoId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Listado de grupos" />

      <FiltroGrupoForm
        onBuscar={(val) => {
          filtros.current = val
          dataProvider.refresh()
        }}
        onNuevo={() => setDialogOpened(true)}
      />

      <Grid dataProvider={dataProvider}>
        <GridColumn path="nombre" />
        <GridColumn path="nombreProfesor" />
        <GridColumn path="activo" renderer={({ item }) => <>{item.activo ? 'Sí' : 'No'}</>} />
        <GridColumn header="Acciones" renderer={accionesTabla} />
      </Grid>

      <Dialog
        opened={dialogOpened}
        noCloseOnOutsideClick
        headerTitle='Registrar grupo'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <GrupoForm
          grupoId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}
