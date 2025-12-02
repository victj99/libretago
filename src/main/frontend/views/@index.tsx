import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import { useAuth } from 'Frontend/security/auth';
import StudentStatsChart from 'Frontend/components/charts/StudentStatsChart';
import ProfesorStatsChart from 'Frontend/components/charts/ProfesorStatsChart';
import NotificationStatsChart from 'Frontend/components/charts/NotificationStatsChart';
import ColegioStatsChart from 'Frontend/components/charts/ColegioStatsChart';

export const config: ViewConfig = {
  title: 'Inicio',
  menu: {
    icon: 'vaadin:home',
    title: 'Inicio',
  },
}

export default function TaskListView() {
  const { hasAccess } = useAuth();
  const isColegio = hasAccess({ rolesAllowed: ['COLEGIO'] });
  const isAdmin = hasAccess({ rolesAllowed: ['ADMIN'] });

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Inicio" />
      {isColegio && (
        <div className="flex-grow flex flex-col items-center justify-center gap-m">
          <div className="flex flex-row flex-wrap justify-center gap-m w-full">
            <StudentStatsChart />
            <ProfesorStatsChart />
          </div>
          <NotificationStatsChart />
        </div>
      )}
      {isAdmin && (
        <div className="flex-grow flex flex-col items-center justify-center gap-m">
          <div className="flex flex-row flex-wrap justify-center gap-m w-full">
            <ColegioStatsChart />
          </div>
        </div>
      )}
    </main>
  )
}
