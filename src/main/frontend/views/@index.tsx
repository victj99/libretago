import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'

export const config: ViewConfig = {
  title: 'Inicio',
  menu: {
    icon: 'vaadin:home',
    title: 'Inicio',
  },
}

export default function TaskListView() {

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Inicio" />
    </main>
  )
}
