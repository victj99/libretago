import { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { AlumnoEndpoint } from 'Frontend/generated/endpoints';
import StudentStatsDTO from 'Frontend/generated/com/utp/libretago/classes/dto/StudentStatsDTO';

export default function StudentStatsChart() {
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

  if (!stats) {
    return <div>Cargando estadísticas de alumnos...</div>;
  }

  return (
    <ReactECharts option={option} style={{ height: '400px', width: '45%', minWidth: '300px' }} />
  );
}
