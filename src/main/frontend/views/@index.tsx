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
import { AlumnoEndpoint } from 'Frontend/generated/endpoints';
import StudentStatsDTO from 'Frontend/generated/com/utp/libretago/classes/dto/StudentStatsDTO';

export default function TaskListView() {
  const [stats, setStats] = useState<StudentStatsDTO | null>(null);

  useEffect(() => {
    AlumnoEndpoint.obtenerEstadisticas().then(setStats).catch(console.error);
  }, []);

  const option = stats ? {
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
          { value: stats.activeCount, name: 'Activos' },
          { value: stats.inactiveCount, name: 'Inactivos' }
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
      <div className="flex-grow flex flex-col items-center justify-center gap-m">
        {stats ? (
          <ReactECharts option={option} style={{ height: '400px', width: '100%' }} />
        ) : (
          <div>Cargando estadísticas de alumnos...</div>
        )}
        <NotificationChart />
      </div>
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
