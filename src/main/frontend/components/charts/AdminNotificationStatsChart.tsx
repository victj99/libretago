import { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { LoggedUserService } from 'Frontend/generated/endpoints';
import NotificationStatsMultiLineDTO from 'Frontend/generated/com/utp/libretago/classes/dto/NotificationStatsMultiLineDTO';

export default function AdminNotificationStatsChart() {
  const [data, setData] = useState<NotificationStatsMultiLineDTO[]>([]);

  useEffect(() => {
    LoggedUserService.obtenerEstadisticasNotificacionesTodasInstituciones()
      .then((result) => {
        if (result) setData(result);
      })
      .catch(console.error);
  }, []);

  // Obtener todas las fechas únicas de todas las instituciones
  const allDates = [
    ...new Set(
      data.flatMap((inst) => inst.stats?.filter((s) => s != null).map((s) => s!.date) ?? [])
    ),
  ].sort();

  const option = {
    title: {
      text: 'Notificaciones por Colegio (ult. 2 meses)',
      left: 'center',
    },
    tooltip: {
      trigger: 'axis',
    },
    legend: {
      data: data.map((inst) => inst.institucionNombre),
      top: 50,
      type: 'scroll',
    },
    grid: {
      top: 100,
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: allDates,
    },
    yAxis: {
      type: 'value',
    },
    series: data.map((inst) => ({
      name: inst.institucionNombre,
      type: 'line',
      smooth: true,
      data: allDates.map((date) => {
        const stat = inst.stats?.filter((s) => s != null).find((s) => s!.date === date);
        return stat ? stat.count : 0;
      }),
    })),
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
