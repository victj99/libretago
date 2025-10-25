export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: ReturnType<typeof setTimeout> | null = null

  return function (...args: Parameters<T>) {
    // Cancelar el timeout anterior si existe
    if (timeoutId) {
      clearTimeout(timeoutId)
    }

    // Crear nuevo timeout
    timeoutId = setTimeout(() => {
      func(...args)
    }, delay)
  }
}