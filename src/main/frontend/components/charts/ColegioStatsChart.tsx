import { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { UsuarioInstitucionEndpoint } from 'Frontend/generated/endpoints';
import ColegioStatsDTO from 'Frontend/generated/com/utp/libretago/classes/dto/ColegioStatsDTO';

export default function ColegioStatsChart() {
  const [stats, setStats] = useState<ColegioStatsDTO | null>(null);

  useEffect(() => {
    UsuarioInstitucionEndpoint.obtenerEstadisticas().then(data => {
      if (data) setStats(data);
    }).catch(console.error);
  }, []);

  const option = stats ? {
    title: {
      text: 'Estado de Usuarios',
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
        name: 'Colegios',
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
    return <div>Cargando estadísticas de colegios...</div>;
  }

  return (
    <ReactECharts option={option} style={{ height: '400px', width: '45%', minWidth: '300px' }} />
  );
}
