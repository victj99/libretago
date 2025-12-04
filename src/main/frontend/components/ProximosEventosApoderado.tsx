import { useEffect, useState } from 'react';
import EventoDTO from 'Frontend/generated/com/utp/libretago/classes/dto/EventoDTO';
import Pageable from 'Frontend/generated/com/vaadin/hilla/mappedtypes/Pageable';
import { EventoUsuarioEndpoint } from 'Frontend/generated/endpoints';

// Nombres de los días de la semana
const DIAS_SEMANA = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];

// Nombres de los meses
const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
];

export default function ProximosEventosApoderado() {
  const [eventos, setEventos] = useState<EventoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [mesActual, setMesActual] = useState(new Date());
  const [fechaSeleccionada, setFechaSeleccionada] = useState(new Date());

  // Cargar eventos al montar el componente
  useEffect(() => {
    cargarEventos();
  }, []);

  async function cargarEventos() {
    setLoading(true);
    try {
      const pageRequest: Pageable = { pageNumber: 0, pageSize: 100, sort: { orders: [] } };
      const respuesta: any = await EventoUsuarioEndpoint.listarEventosUsuario(pageRequest, 'RECIBIDAS');

      if (Array.isArray(respuesta)) {
        setEventos(respuesta);
      } else if (respuesta && Array.isArray(respuesta.content)) {
        setEventos(respuesta.content);
      } else {
        setEventos([]);
      }
    } catch (err) {
      console.error('Error cargando eventos', err);
    } finally {
      setLoading(false);
    }
  }

  // Obtener días del mes
  function obtenerDiasDelMes(fecha: Date) {
    const anio = fecha.getFullYear();
    const mes = fecha.getMonth();

    const primerDia = new Date(anio, mes, 1);
    const ultimoDia = new Date(anio, mes + 1, 0);

    const dias: (number | null)[] = [];

    // Días vacíos al inicio
    for (let i = 0; i < primerDia.getDay(); i++) {
      dias.push(null);
    }

    // Días del mes
    for (let i = 1; i <= ultimoDia.getDate(); i++) {
      dias.push(i);
    }

    return dias;
  }

  // Verificar si un día tiene eventos
  function diaConEventos(dia: number): boolean {
    const fechaDia = new Date(mesActual.getFullYear(), mesActual.getMonth(), dia);
    return eventos.some(evento => {
      if (!evento.fechaEvento) return false;
      const fechaEvento = new Date(evento.fechaEvento);
      return fechaEvento.toDateString() === fechaDia.toDateString();
    });
  }

  // Verificar si es el día seleccionado
  function esDiaSeleccionado(dia: number): boolean {
    const fechaDia = new Date(mesActual.getFullYear(), mesActual.getMonth(), dia);
    return fechaDia.toDateString() === fechaSeleccionada.toDateString();
  }

  // Verificar si es hoy
  function esHoy(dia: number): boolean {
    const fechaDia = new Date(mesActual.getFullYear(), mesActual.getMonth(), dia);
    return fechaDia.toDateString() === new Date().toDateString();
  }

  // Obtener eventos del día seleccionado
  function eventosDelDia(): EventoDTO[] {
    return eventos.filter(evento => {
      if (!evento.fechaEvento) return false;
      const fechaEvento = new Date(evento.fechaEvento);
      return fechaEvento.toDateString() === fechaSeleccionada.toDateString();
    }).sort((a, b) => {
      const fechaA = new Date(a.fechaEvento!);
      const fechaB = new Date(b.fechaEvento!);
      return fechaA.getTime() - fechaB.getTime();
    });
  }

  // Navegar mes anterior
  function mesAnterior() {
    setMesActual(new Date(mesActual.getFullYear(), mesActual.getMonth() - 1, 1));
  }

  // Navegar mes siguiente
  function mesSiguiente() {
    setMesActual(new Date(mesActual.getFullYear(), mesActual.getMonth() + 1, 1));
  }

  // Seleccionar día
  function seleccionarDia(dia: number) {
    setFechaSeleccionada(new Date(mesActual.getFullYear(), mesActual.getMonth(), dia));
  }

  // Formatear hora
  function formatearHora(fechaStr: string): string {
    const fecha = new Date(fechaStr);
    return fecha.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
  }

  // Formatear fecha para título
  function formatearFechaTitulo(): string {
    return `${fechaSeleccionada.getDate()} de ${MESES[fechaSeleccionada.getMonth()]}`;
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 border-4 border-blue-300 border-t-blue-500 rounded-full animate-spin"></div>
          <span className="text-sm text-gray-500">Cargando eventos...</span>
        </div>
      </div>
    );
  }

  const diasDelMes = obtenerDiasDelMes(mesActual);
  const eventosHoy = eventosDelDia();

  return (
    <div className="w-full max-w-md mx-auto">
      {/* Contenedor principal con estilo de tarjeta */}
      <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl shadow-lg border border-blue-200 overflow-hidden">

        {/* Cabecera del calendario */}
        <div className="bg-gradient-to-r from-blue-400 to-indigo-400 text-white px-6 py-4">
          <h2 className="text-xl font-bold text-center mb-3 text-white">Próximos Eventos</h2>

          <div className="flex items-center justify-between">
            <button
              onClick={mesAnterior}
              className="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 hover:bg-white/30 transition-colors"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
            </button>

            <span className="text-lg font-semibold">
              {MESES[mesActual.getMonth()]} {mesActual.getFullYear()}
            </span>

            <button
              onClick={mesSiguiente}
              className="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 hover:bg-white/30 transition-colors"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        {/* Días de la semana */}
        <div className="grid grid-cols-7 gap-1 px-4 py-2 bg-blue-100/50">
          {DIAS_SEMANA.map((dia, index) => (
            <div
              key={dia}
              className={`text-center text-xs font-semibold py-1 ${index === 0 || index === 6 ? 'text-blue-600' : 'text-gray-600'
                }`}
            >
              {dia}
            </div>
          ))}
        </div>

        {/* Días del mes */}
        <div className="grid grid-cols-7 gap-1 px-4 py-2">
          {diasDelMes.map((dia, index) => (
            <div key={index} className="aspect-square flex items-center justify-center">
              {dia !== null && (
                <button
                  onClick={() => seleccionarDia(dia)}
                  className={`w-9 h-9 rounded-full flex flex-col items-center justify-center text-sm font-medium transition-all relative
                    ${esDiaSeleccionado(dia)
                      ? 'bg-gradient-to-br from-blue-500 to-indigo-600 text-white shadow-md'
                      : esHoy(dia)
                        ? 'bg-blue-200 text-blue-800'
                        : 'hover:bg-blue-100 text-gray-700'
                    }
                    ${(index % 7 === 0 || index % 7 === 6) && !esDiaSeleccionado(dia) ? 'text-blue-600' : ''}
                  `}
                >
                  {dia}
                  {diaConEventos(dia) && !esDiaSeleccionado(dia) && (
                    <span className="absolute bottom-0.5 w-1.5 h-1.5 bg-indigo-500 rounded-full"></span>
                  )}
                </button>
              )}
            </div>
          ))}
        </div>

        {/* Sección de eventos del día seleccionado */}
        <div className="border-t border-blue-200 px-4 py-4 bg-white/60">
          <h3 className="text-base font-bold text-gray-800 mb-3">
            Eventos para el {formatearFechaTitulo()}
          </h3>

          {eventosHoy.length === 0 ? (
            <div className="text-center py-4">
              <div className="w-12 h-12 mx-auto mb-2 rounded-full bg-gray-100 flex items-center justify-center">
                <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <p className="text-sm text-gray-500">No hay eventos programados</p>
            </div>
          ) : (
            <div className="space-y-3">
              {eventosHoy.map((evento, index) => (
                <div
                  key={evento.id}
                  className={`flex items-center gap-3 p-3 rounded-xl transition-all hover:shadow-md ${index % 2 === 0
                    ? 'bg-gradient-to-r from-blue-100 to-indigo-100 border border-blue-200'
                    : 'bg-gradient-to-r from-indigo-100 to-purple-100 border border-indigo-200'
                    }`}
                >
                  {/* Icono */}
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${index % 2 === 0 ? 'bg-blue-200' : 'bg-indigo-200'
                    }`}>
                    <svg className={`w-5 h-5 ${index % 2 === 0 ? 'text-blue-600' : 'text-indigo-600'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>

                  {/* Contenido */}
                  <div className="flex-1 min-w-0">
                    <h4 className="text-sm font-semibold text-gray-800 truncate">
                      {evento.titulo}
                    </h4>
                    <p className="text-xs text-gray-600">
                      {formatearHora(evento.fechaEvento!)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
