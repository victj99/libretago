import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { NotificacionForm } from 'Frontend/components/forms/NotificacionForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import { useNavigate } from 'react-router'

export const config: ViewConfig = {
  title: 'Nueva Notificación',
  rolesAllowed: ['PROFESOR'],
  menu: { exclude: true }
}

export default function NuevaNotificacionView() {
  const navigate = useNavigate()

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Nueva Notificación" />
      <div className="max-w-3xl w-full mx-auto bg-white rounded-lg shadow-sm p-6">
        <NotificacionForm
          onCerrar={() => navigate('/notificaciones')}
        />
      </div>
    </main>
  )
}
