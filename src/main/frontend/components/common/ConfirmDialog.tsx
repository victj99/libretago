import { ConfirmDialog, ConfirmDialogProps } from "@vaadin/react-components/ConfirmDialog"
import { createContext, useContext, useState, useCallback, ReactNode } from "react"

interface ConfirmDialogOptions {
  header: string
  text: string
  confirmText?: string
  cancelText?: string
  rejectText?: string
  cancelable?: boolean
  rejectable?: boolean
  confirmTheme?: string
  cancelTheme?: string
  rejectTheme?: string
}

interface ConfirmState extends ConfirmDialogOptions {
  opened: boolean
  resolve: (result: boolean) => void
}

const ConfirmContext = createContext<((options: ConfirmDialogOptions) => Promise<boolean>) | null>(null)

export function ConfirmProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<ConfirmState | null>(null)

  const showConfirmation = useCallback((options: ConfirmDialogOptions): Promise<boolean> => {
    return new Promise((resolve) => {
      setState({
        ...options,
        opened: true,
        resolve,
      })
    })
  }, [])

  const handleClose = (result: boolean) => {
    if (state) {
      state.resolve(result)
      setState(null)
    }
  }

  const dialogProps: ConfirmDialogProps = state ? {
    opened: state.opened,
    header: state.header,
    confirmText: state.confirmText || "Confirmar",
    cancelText: state.cancelText || "Cancelar",
    rejectText: state.rejectText || "Rechazar",
    onConfirm: () => handleClose(true),
    onCancel: () => handleClose(false),
    onReject: () => handleClose(false),
    cancelButtonVisible: state.cancelable,
    rejectButtonVisible: state.rejectable,
    confirmTheme: state.confirmTheme || 'primary',
    cancelTheme: state.cancelTheme,
    rejectTheme: state.rejectTheme,
  } : { opened: false }

  return (
    <ConfirmContext.Provider value={showConfirmation}>
      {children}
      {state && (
        <ConfirmDialog {...dialogProps}>
          {state.text}
        </ConfirmDialog>
      )}
    </ConfirmContext.Provider>
  )
}

export function useConfirm() {
  const context = useContext(ConfirmContext)
  if (!context) {
    throw new Error("useConfirm must be used within ConfirmProvider")
  }
  return context
}