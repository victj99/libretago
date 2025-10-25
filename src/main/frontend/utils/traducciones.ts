export const UPLOAD_ESP = {
  dropFiles: {
    one: 'Suelta el archivo aquí',
    many: 'Suelta los archivos aquí'
  },
  addFiles: {
    one: 'Subir archivo...',
    many: 'Subir archivos...'
  },
  error: {
    tooManyFiles: 'Demasiados archivos.',
    fileIsTooBig: 'El archivo es demasiado grande.',
    incorrectFileType: 'Tipo de archivo incorrecto.'
  },
  uploading: {
    status: {
      connecting: 'Conectando...',
      stalled: 'Detenido',
      processing: 'Procesando archivo...',
      held: 'En cola'
    },
    remainingTime: {
      prefix: 'tiempo restante: ',
      unknown: 'tiempo restante desconocido'
    },
    error: {
      serverUnavailable: 'Error de subida, por favor intenta más tarde',
      unexpectedServerError: 'Error en el servidor durante la subida',
      forbidden: 'Subida prohibida'
    }
  },
  file: {
    retry: 'Reintentar',
    start: 'Iniciar',
    remove: 'Eliminar'
  },
  units: {
    size: ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
    sizeBase: 1000
  },
}
