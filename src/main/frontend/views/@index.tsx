import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'

export const config: ViewConfig = {
  title: 'Inicio',
  menu: {
    icon: 'vaadin:home',
    title: 'Inicio',
  },
}

import { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { AlumnoEndpoint, UsuarioProfesorEndpoint } from 'Frontend/generated/endpoints';
import StudentStatsDTO from 'Frontend/generated/com/utp/libretago/classes/dto/StudentStatsDTO';
import ProfesorStatsDTO from 'Frontend/generated/com/utp/libretago/classes/dto/ProfesorStatsDTO';

import { useAuth } from 'Frontend/security/auth';

export default function TaskListView() {
  const [studentStats, setStudentStats] = useState<StudentStatsDTO | null>(null);
  const [profesorStats, setProfesorStats] = useState<ProfesorStatsDTO | null>(null);
  const { hasAccess } = useAuth();
  const isColegio = hasAccess({ rolesAllowed: ['COLEGIO'] });

  useEffect(() => {
    if (isColegio) {
      AlumnoEndpoint.obtenerEstadisticas().then(setStudentStats).catch(console.error);
      UsuarioProfesorEndpoint.obtenerEstadisticas().then(data => {
        if (data) setProfesorStats(data);
      }).catch(console.error);
    }
  }, [isColegio]);

  const studentOption = studentStats ? {
    title: {
      text: 'Estado de Alumnos',
      subtext: 'Activos vs Inactivos',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: 'Alumnos',
        type: 'pie',
        radius: '50%',
        label: {
          show: true,
          formatter: '{b}: {c} ({d}%)'
        },
        data: [
          { value: studentStats.activeCount, name: 'Activos' },
          { value: studentStats.inactiveCount, name: 'Inactivos' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  } : {};

  const profesorOption = profesorStats ? {
    title: {
      text: 'Estado de Profesores',
      subtext: 'Activos vs Inactivos',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: 'Profesores',
        type: 'pie',
        radius: '50%',
        label: {
          show: true,
          formatter: '{b}: {c} ({d}%)'
        },
        data: [
          { value: profesorStats.activeCount, name: 'Activos' },
          { value: profesorStats.inactiveCount, name: 'Inactivos' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  } : {};

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Inicio" />
      {isColegio && (
        <div className="flex-grow flex flex-col items-center justify-center gap-m">
          <div className="flex flex-row flex-wrap justify-center gap-m w-full">
            {studentStats ? (
              <ReactECharts option={studentOption} style={{ height: '400px', width: '45%', minWidth: '300px' }} />
            ) : (
              <div>Cargando estadísticas de alumnos...</div>
            )}
            {profesorStats ? (
              <ReactECharts option={profesorOption} style={{ height: '400px', width: '45%', minWidth: '300px' }} />
            ) : (
              <div>Cargando estadísticas de profesores...</div>
            )}
          </div>
          <NotificationChart />
        </div>
      )}
    </main>
  )
}

function NotificationChart() {
  const [data, setData] = useState<any[]>([]);

  useEffect(() => {
    import('Frontend/generated/endpoints').then(({ AdministrarNotificacionEndpoint }) => {
      AdministrarNotificacionEndpoint.obtenerEstadisticas().then(setData).catch(console.error);
    });
  }, []);

  const option = {
    title: {
      text: 'Notificaciones Enviadas',
      subtext: 'Últimos 2 meses',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: data.map(item => item.count),
        type: 'line',
        smooth: true
      }
    ]
  };

  return (
    <div className="w-full">
      {data.length > 0 ? (
        <ReactECharts option={option} style={{ height: '400px', width: '100%' }} />
      ) : (
        <div className="text-center">Cargando estadísticas de notificaciones...</div>
      )}
    </div>
  );
}
