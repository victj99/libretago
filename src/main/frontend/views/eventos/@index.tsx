import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { Button, Tab, Tabs } from '@vaadin/react-components'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import EventoDTO from 'Frontend/generated/com/utp/libretago/classes/dto/EventoDTO'
import Pageable from 'Frontend/generated/com/vaadin/hilla/mappedtypes/Pageable'
import { EventoUsuarioEndpoint } from 'Frontend/generated/endpoints'
import { useAuth } from 'Frontend/security/auth'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'


export const config: ViewConfig = {
  title: 'Eventos',
  menu: {
    title: 'Eventos',
  },
  rolesAllowed: ['PROFESOR', 'APODERADO']
}

export default function EventosView() {
  const [items, setItems] = useState<EventoDTO[]>([])
  const [loading, setLoading] = useState(false)
  const [tab, setTab] = useState(0) // 0: Recibidas, 1: Enviadas
  const navigate = useNavigate()
  const { hasAccess } = useAuth()
  const isProfesor = hasAccess({ rolesAllowed: ['PROFESOR'] });


  async function cargarEventos(mounted: boolean, currentTab: number) {
    setLoading(true)
    try {
      const pageRequest: Pageable = { pageNumber: 0, pageSize: 50, sort: { orders: [] } }
      const tipo = currentTab === 1 ? 'ENVIADAS' : 'RECIBIDAS'
      const respuesta: any = await EventoUsuarioEndpoint.listarEventosUsuario(pageRequest, tipo)

      if (!mounted) return

      if (Array.isArray(respuesta)) {
        setItems(respuesta)
      } else if (respuesta && Array.isArray(respuesta.content)) {
        setItems(respuesta.content)
      } else {
        setItems([])
      }

    } catch (err) {
      console.error('Error cargando eventos', err)
    } finally {
      if (mounted) setLoading(false)
    }
  }

  useEffect(() => {
    let mounted = true
    cargarEventos(mounted, tab)

    return () => { mounted = false }
  }, [tab])

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Eventos">
        {isProfesor && (
          <div className="flex justify-end w-full">
            <Button theme="primary" onClick={() => navigate('/eventos/registro')}>Nuevo</Button>
          </div>
        )}
      </ViewToolbar>

      {isProfesor && (
        <Tabs selected={tab} onSelectedChanged={(e) => setTab(e.detail.value)}>
          <Tab>Recibidos</Tab>
          <Tab>Enviados</Tab>
        </Tabs>
      )}

      <section className="flex flex-col gap-4 md:gap-6">
        {loading && <div className="text-center text-s">Cargando...</div>}

        {items.length === 0 && !loading && (
          <div className="text-center text-s">No hay eventos</div>
        )}

        {items.map((item) => (
          <article key={item.id} className="flex items-start gap-4 p-4 rounded-lg shadow-sm bg-blue-50">

            <div className="flex-1">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-semibold leading-tight">{item.titulo}</h3>
                <span className="text-xs text-gray-600">{item.fechaEvento?.substring(0, 16).replace('T', ' ')}</span>
              </div>

              <p className="text-xs text-gray-800 mt-2 line-clamp-3">{item.detalle}</p>

              {item.grupos && item.grupos.length > 0 && (
                <div className="mt-3 text-xs text-gray-700">
                  <strong>Grupos: </strong>{item.grupos.map(g => g.label).join(', ')}
                </div>
              )}
            </div>
          </article>
        ))}
      </section>
    </main>
  )
}
