import { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { UsuarioProfesorEndpoint } from 'Frontend/generated/endpoints';
import ProfesorStatsDTO from 'Frontend/generated/com/utp/libretago/classes/dto/ProfesorStatsDTO';

export default function ProfesorStatsChart() {
  const [stats, setStats] = useState<ProfesorStatsDTO | null>(null);

  useEffect(() => {
    UsuarioProfesorEndpoint.obtenerEstadisticas().then(data => {
      if (data) setStats(data);
    }).catch(console.error);
  }, []);

  const option = stats ? {
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
    return <div>Cargando estadísticas de profesores...</div>;
  }

  return (
    <ReactECharts option={option} style={{ height: '400px', width: '45%', minWidth: '300px' }} />
  );
}
