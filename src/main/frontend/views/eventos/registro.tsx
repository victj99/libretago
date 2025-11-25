import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { EventoForm } from 'Frontend/components/forms/EventoForm'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import { useNavigate } from 'react-router'


export const config: ViewConfig = {
  title: 'Registro de Evento',
  menu: {
    exclude: true
  },
  rolesAllowed: ['PROFESOR', 'COLEGIO']
}

export default function EventoRegistroView() {
  const navigate = useNavigate()

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Registro de Evento" />

      <section className="flex flex-col gap-4 md:gap-6 max-w-screen-sm mx-auto w-full bg-white p-6 rounded-lg shadow-sm">
        <EventoForm onCerrar={() => navigate(-1)} />
      </section>
    </main>
  )
}
