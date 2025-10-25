import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { useState } from 'react'
import { Button, Grid, GridCellPartNameGenerator, GridColumn, Notification, Upload, UploadElement, UploadRequestEvent } from '@vaadin/react-components'
import { ViewToolbar } from 'Frontend/components/ViewToolbar'
import AlumnoDTO from 'Frontend/generated/com/utp/libretago/classes/dto/AlumnoDTO'
import { AlumnoEndpoint } from 'Frontend/generated/endpoints'
import { UPLOAD_ESP } from 'Frontend/utils/traducciones'
import { useNavigate } from 'react-router'
import handleError from '../_ErrorHandler'

export const config: ViewConfig = {
  menu: { exclude: true },
  rolesAllowed: ['COLEGIO']
}

export default function RegistroMasivoAlumnoView() {
  const navigate = useNavigate()

  const [data, setData] = useState<AlumnoDTO[]>([])
  const [archivoErroresId, setArchivoErroresId] = useState<string | null>(null)

  async function handleUploadRequest(e: UploadRequestEvent) {
    e.preventDefault()
    setArchivoErroresId(null)

    const uploadRef = e.target as UploadElement

    try {
      setData([])
      const resultados = await AlumnoEndpoint.validarArchivo(e.detail.file)

      if (resultados?.archivoId) {
        setArchivoErroresId(resultados.archivoId)
        Notification.show('El archivo contiene errores. Descargue el reporte para más detalles.', {
          position: 'bottom-end', theme: 'error'
        })
      } else if (resultados?.datosCargados) {
        setData(resultados.datosCargados)
        Notification.show('Archivo validado correctamente', {
          position: 'bottom-end', theme: 'success'
        })
      }
    } catch (error) {
      handleError(error)
    }

    uploadRef.files = uploadRef.files.map((file) => {
      file.status = ''
      file.complete = true
      return file
    })
  }

  function descargarErrores() {
    if (archivoErroresId) {
      window.open(`/descargar/excelErrores/${archivoErroresId}`, '_blank')
    }
  }

  async function registrarAlumnos() {
    try {
      for (const alumno of data) {
        if (alumno.id) {
          await AlumnoEndpoint.editarAlumno(alumno.id, { ...alumno, dniCeApoderado: '-', nombreCompletoApoderado: '-' })
        } else {
          await AlumnoEndpoint.crearAlumno(alumno)
        }
      }

      Notification.show('Alumnos registrados correctamente', {
        position: 'bottom-end',
        theme: 'success'
      })

      // Limpiar datos
      setData([])
      setArchivoErroresId(null)

      navigate('/alumnos')
    } catch (error) {
      Notification.show('Error al registrar los alumnos', {
        position: 'bottom-end',
        theme: 'error'
      })
    }
  }

  const cellPartNameGenerator: GridCellPartNameGenerator<AlumnoDTO> = (column, model) => {
    const item = model.item
    let parts = ''

    if (item.id) {
      parts += ' editar-registro'
    }
    return parts
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Registro masivo de alumnos" />

      <div className='flex flex-wrap items-center gap-2'>
        <Upload
          maxFiles={1}
          accept='.xlsx'
          i18n={{ ...UPLOAD_ESP }}
          onUploadRequest={handleUploadRequest}
        />

        {archivoErroresId && (
          <Button theme='error' onClick={descargarErrores}>
            Descargar errores
          </Button>
        )}

        {data.length > 0 && (
          <Button theme='primary' onClick={registrarAlumnos}>
            Registrar alumnos
          </Button>
        )}
      </div>

      <Grid items={data} theme='row-stripes' cellPartNameGenerator={cellPartNameGenerator}>
        <GridColumn path="nombres" />
        <GridColumn path="apellidos" />
        <GridColumn path="codigoAlumno" />
        <GridColumn path="telefono" />
        <GridColumn path="email" />
        <GridColumn path="dniCeApoderado" header='DNI/Ce' />
        <GridColumn path="nombreCompletoApoderado" header='Apoderado' />
        <GridColumn path="id" header="Acción">
          {({ item }) => item.id ? 'Actualización' : 'Nuevo'}
        </GridColumn>
      </Grid>
    </main>
  )
}
