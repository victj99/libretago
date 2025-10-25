import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import NotificacionDTO from 'Frontend/generated/com/utp/libretago/classes/dto/NotificacionDTO'
import Pageable from 'Frontend/generated/com/vaadin/hilla/mappedtypes/Pageable'
import { NotificacionUsuarioEndpoint } from 'Frontend/generated/endpoints'
import { useEffect, useState } from 'react'


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


  async function cargarNotificaciones(mounted: boolean) {
    setLoading(true)
    try {
      const pageRequest: Pageable = { pageNumber: 0, pageSize: 50, sort: { orders: [] } }
      const respuesta: any = await NotificacionUsuarioEndpoint.listarNotificacionesUsuario(pageRequest)

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
    cargarNotificaciones(mounted)

    return () => { mounted = false }
  }, [])

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Notificaciones" />

      <section className="flex flex-col gap-4 md:gap-6">
        {loading && <div className="text-center text-s">Cargando...</div>}

        {items.length === 0 && !loading && (
          <div className="text-center text-s">No hay notificaciones</div>
        )}

        {items.map((item) => (
          <article key={item.id} className="flex items-start gap-4 p-4 rounded-lg shadow-sm bg-yellow-50">

            <div className="flex-1">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-semibold leading-tight">{item.titulo}</h3>
                <span className="text-xs text-gray-600">{item.fechaEvaluacion?.split('T')[0]}</span>
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
