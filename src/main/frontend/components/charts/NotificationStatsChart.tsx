import { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { AdministrarNotificacionEndpoint } from 'Frontend/generated/endpoints'

export default function NotificationStatsChart() {
  const [data, setData] = useState<any[]>([]);

  useEffect(() => {
    AdministrarNotificacionEndpoint.obtenerEstadisticas().then(setData).catch(console.error);
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

  if (data.length === 0) {
    return <div className="text-center">Cargando estadísticas de notificaciones...</div>;
  }

  return (
    <div className="w-full">
      <ReactECharts option={option} style={{ height: '400px', width: '100%' }} />
    </div>
  );
}
