import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { Button, Tab, Tabs } from '@vaadin/react-components'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import NotificacionDTO from 'Frontend/generated/com/utp/libretago/classes/dto/NotificacionDTO'
import Pageable from 'Frontend/generated/com/vaadin/hilla/mappedtypes/Pageable'
import { NotificacionUsuarioEndpoint } from 'Frontend/generated/endpoints'
import { useAuth } from 'Frontend/security/auth'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'


export const config: ViewConfig = {
  title: 'Notificaciones',
  menu: {
    title: 'Notificaciones',
  },
  rolesAllowed: ['PROFESOR', 'APODERADO']
}

export default function NotificacionesView() {
  const [items, setItems] = useState<NotificacionDTO[]>([])
  const [loading, setLoading] = useState(false)
  const [tab, setTab] = useState(0) // 0: Recibidas, 1: Enviadas
  const navigate = useNavigate()
  const { hasAccess } = useAuth()
  const isProfesor = hasAccess({ rolesAllowed: ['PROFESOR'] });


  async function cargarNotificaciones(mounted: boolean, currentTab: number) {
    setLoading(true)
    try {
      const pageRequest: Pageable = { pageNumber: 0, pageSize: 50, sort: { orders: [] } }
      const tipo = currentTab === 1 ? 'ENVIADAS' : 'RECIBIDAS'
      const respuesta: any = await NotificacionUsuarioEndpoint.listarNotificacionesUsuario(pageRequest, tipo)

      if (!mounted) return

      // normalizar: puede venir Page { content: [] } o un array directo
      if (Array.isArray(respuesta)) {
        setItems(respuesta)
      } else if (respuesta && Array.isArray(respuesta.content)) {
        setItems(respuesta.content)
      } else {
        setItems([])
      }

    } catch (err) {
      console.error('Error cargando notificaciones', err)
    } finally {
      if (mounted) setLoading(false)
    }
  }

  useEffect(() => {
    let mounted = true
    cargarNotificaciones(mounted, tab)

    return () => { mounted = false }
  }, [tab])

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Notificaciones">
        {isProfesor && (
          <div className="flex justify-end w-full">
            <Button theme="primary" onClick={() => navigate('/notificaciones/registro')}>Nuevo</Button>
          </div>
        )}
      </ViewToolbar>

      {isProfesor && (
        <Tabs selected={tab} onSelectedChanged={(e) => setTab(e.detail.value)}>
          <Tab>Recibidas</Tab>
          <Tab>Enviadas</Tab>
        </Tabs>
      )}

      <section className="flex-1 overflow-auto">
        {loading && (
          <div className="flex items-center justify-center py-12">
            <div className="flex flex-col items-center gap-3">
              <div className="w-10 h-10 border-4 border-yellow-300 border-t-yellow-500 rounded-full animate-spin"></div>
              <span className="text-sm text-gray-500">Cargando notificaciones...</span>
            </div>
          </div>
        )}

        {items.length === 0 && !loading && (
          <div className="flex flex-col items-center justify-center py-16 text-center">
            <div className="w-20 h-20 mb-4 rounded-full bg-yellow-100 flex items-center justify-center">
              <svg className="w-10 h-10 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
            </div>
            <h3 className="text-lg font-medium text-gray-700 mb-1">Sin notificaciones</h3>
            <p className="text-sm text-gray-500">No tienes notificaciones por el momento</p>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 md:gap-5">
          {items.map((item) => (
            <article 
              key={item.id} 
              className="group relative flex flex-col p-5 rounded-xl shadow-md bg-gradient-to-br from-yellow-50 to-amber-50 border border-yellow-200 hover:shadow-lg hover:border-yellow-300 transition-all duration-300 hover:-translate-y-1"
            >
              {/* Indicador decorativo */}
              <div className="absolute top-0 left-5 w-12 h-1 bg-gradient-to-r from-yellow-400 to-amber-500 rounded-b-full"></div>
              
              {/* Cabecera con fecha */}
              <div className="flex items-start justify-between mb-3 pt-2">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-yellow-200 flex items-center justify-center flex-shrink-0">
                    <svg className="w-4 h-4 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                    </svg>
                  </div>
                  <h3 className="text-base font-semibold text-gray-800 leading-snug line-clamp-2 group-hover:text-yellow-700 transition-colors">
                    {item.titulo}
                  </h3>
                </div>
              </div>

              {/* Fecha */}
              <div className="flex items-center gap-1.5 text-xs text-gray-500 mb-3">
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span>{item.fechaEvaluacion?.split('T')[0]}</span>
              </div>

              {/* Contenido */}
              <p className="text-sm text-gray-600 leading-relaxed line-clamp-3 flex-1 mb-3">
                {item.detalle}
              </p>

              {/* Grupos */}
              {item.grupos && item.grupos.length > 0 && (
                <div className="pt-3 border-t border-yellow-200">
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
                        className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-yellow-200 text-yellow-800 border border-yellow-300"
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
