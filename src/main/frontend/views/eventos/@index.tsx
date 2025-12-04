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

      <section className="flex-1 overflow-auto">
        {loading && (
          <div className="flex items-center justify-center py-12">
            <div className="flex flex-col items-center gap-3">
              <div className="w-10 h-10 border-4 border-blue-300 border-t-blue-500 rounded-full animate-spin"></div>
              <span className="text-sm text-gray-500">Cargando eventos...</span>
            </div>
          </div>
        )}

        {items.length === 0 && !loading && (
          <div className="flex flex-col items-center justify-center py-16 text-center">
            <div className="w-20 h-20 mb-4 rounded-full bg-blue-100 flex items-center justify-center">
              <svg className="w-10 h-10 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <h3 className="text-lg font-medium text-gray-700 mb-1">Sin eventos</h3>
            <p className="text-sm text-gray-500">No tienes eventos programados por el momento</p>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 md:gap-5">
          {items.map((item) => (
            <article 
              key={item.id} 
              className="group relative flex flex-col p-5 rounded-xl shadow-md bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-200 hover:shadow-lg hover:border-blue-300 transition-all duration-300 hover:-translate-y-1"
            >
              {/* Indicador decorativo */}
              <div className="absolute top-0 left-5 w-12 h-1 bg-gradient-to-r from-blue-400 to-indigo-500 rounded-b-full"></div>
              
              {/* Cabecera con título */}
              <div className="flex items-start gap-2 mb-3 pt-2">
                <div className="w-8 h-8 rounded-full bg-blue-200 flex items-center justify-center flex-shrink-0">
                  <svg className="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <h3 className="text-base font-semibold text-gray-800 leading-snug line-clamp-2 group-hover:text-blue-700 transition-colors">
                  {item.titulo}
                </h3>
              </div>

              {/* Fechas */}
              <div className="flex flex-col gap-1.5 mb-3">
                {/* Fecha del evento */}
                <div className="flex items-center gap-1.5 text-xs">
                  <svg className="w-3.5 h-3.5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span className="font-medium text-blue-700">Evento:</span>
                  <span className="text-gray-600">{item.fechaEvento?.substring(0, 16).replace('T', ' ')}</span>
                </div>
              </div>

              {/* Contenido */}
              <p className="text-sm text-gray-600 leading-relaxed line-clamp-3 flex-1 mb-3">
                {item.detalle}
              </p>

              {/* Grupos */}
              {item.grupos && item.grupos.length > 0 && (
                <div className="pt-3 border-t border-blue-200">
                  <div className="flex items-center gap-1.5 mb-2">
                    <svg className="w-3.5 h-3.5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <span className="text-xs font-medium text-gray-600">Grupos:</span>
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {item.grupos.map((g, idx) => (
                      <span 
                        key={idx} 
                        className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-200 text-blue-800 border border-blue-300"
                      >
                        {g.label}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}
