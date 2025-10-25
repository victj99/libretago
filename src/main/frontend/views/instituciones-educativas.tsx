import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useGridDataProvider } from '@vaadin/hilla-react-crud'
import { useForm } from '@vaadin/hilla-react-form'
import { useState } from 'react'
import { Button, Dialog, Grid, GridColumn, Icon, Select, TextField } from '@vaadin/react-components'
import { useConfirm } from 'Frontend/components/common/ConfirmDialog'
import { InstitucionEducativaForm } from 'Frontend/components/forms/InstitucionEducativaForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import FiltroInstitucionEducativa from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroInstitucionEducativa'
import FiltroInstitucionEducativaModel from 'Frontend/generated/com/utp/libretago/classes/filtros/FiltroInstitucionEducativaModel'
import InstitucionEducativa from 'Frontend/generated/com/utp/libretago/entity/InstitucionEducativa'
import { InstitucionEducativaEndpoint } from 'Frontend/generated/endpoints'
import { ACTIVO_TODOS } from 'Frontend/utils/constantes'
import { useRef } from 'react'
import { MdDelete, MdEdit } from 'react-icons/md'

export const config: ViewConfig = {
  title: 'Instituciones educativas',
  menu: {
    title: 'Instituciones educativas',
  },
  rolesAllowed: ['ADMIN']
}

type FiltrosProps = {
  onBuscar: (filtros: FiltroInstitucionEducativa) => void
  onNuevo: () => void
}

export function FiltroInstitucionForm(props: FiltrosProps) {

  const { model, field, submit } = useForm(FiltroInstitucionEducativaModel, {
    onSubmit: async (e) => {
      console.log(e)
      return props.onBuscar(e)
    }
  })

  return <>
    <div className='flex flex-wrap gap-1'>
      <TextField
        className='flex-2'
        aria-label="Nombre de la institución"
        label="Nombre"
        maxlength={255}
        {...field(model.nombre)}
      />
      <TextField
        className='flex-1'
        aria-label="Código Ugel de la institución"
        label="Código Ugel"
        maxlength={255}
        {...field(model.codigoUgel)}
      />

      <Select
        className=''
        aria-label="Activo"
        label="Activo"
        items={ACTIVO_TODOS}
        {...field(model.activo)}
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

export default function InstitucionEducativaView() {
  const confirmDialog = useConfirm()
  const [dialogOpened, setDialogOpened] = useState(false)
  const [editId, setEditId] = useState<undefined | number>(undefined)

  const filtros = useRef<FiltroInstitucionEducativa>({})

  const dataProvider = useGridDataProvider(async (page) => InstitucionEducativaEndpoint.buscarPorFiltros(filtros.current, page), [])

  function accionesTabla(data: { item: InstitucionEducativa }) {
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
      header: 'Desactivar', text: '¿Desea desactivar la institución?',
      cancelable: true, confirmTheme: 'error'
    })
    if (!confirmar) return

    const resp = await InstitucionEducativaEndpoint.inactivarInstitucion(ieId)
    if (resp > 0) dataProvider.refresh()
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Instituciones educativas" />

      <FiltroInstitucionForm
        onBuscar={(val) => {
          filtros.current = val
          dataProvider.refresh()
        }}
        onNuevo={() => setDialogOpened(true)}
      />

      <Grid dataProvider={dataProvider}>
        <GridColumn path="nombre" />
        <GridColumn path="codigoUgel" />
        <GridColumn path="direccion" />
        <GridColumn path="telefono" />
        {/* <GridColumn path="maxProfesores" header='Lim profesores' />
        <GridColumn path="maxAulas" header='Lim aulas' /> */}
        <GridColumn path="activo" renderer={({ item }) => <>{item.activo ? 'Sí' : 'No'}</>} />
        <GridColumn header="Acciones" renderer={accionesTabla} />
      </Grid>

      <Dialog
        opened={dialogOpened}
        noCloseOnOutsideClick
        headerTitle='Registrar institución'
        overlayClass='responsive-dialog'
        onClosed={() => setDialogOpened(false)}>

        <InstitucionEducativaForm
          ieId={editId}
          onCerrar={onCerrarDialog}
        />
      </Dialog>
    </main>
  )
}
