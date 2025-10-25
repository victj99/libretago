import { ArrayModel, NumberModel, StringModel } from "@vaadin/hilla-lit-form"
import { useComboBoxDataProvider } from "@vaadin/hilla-react-crud"
import { useFormPart } from "@vaadin/hilla-react-form"
import { ComboBox, ComboBoxProps, MultiSelectComboBox, MultiSelectComboBoxProps, MultiSelectComboBoxSelectedItemsChangedEvent } from "@vaadin/react-components"
import Pageable from "Frontend/generated/com/vaadin/hilla/mappedtypes/Pageable"
import { useEffect, useState } from "react"

type ComboModel = StringModel | NumberModel

interface ComboBoxFilterProps extends ComboBoxProps<any> {
  fieldModel: ComboModel
  defaultLabel?: string,
  fetcher: (pageable: Pageable, filter: string) => Promise<any[]>
}

export function ComboBoxFilter({
  fetcher, defaultLabel, fieldModel, ...props
}: ComboBoxFilterProps) {
  const { model, value, field } = useFormPart(fieldModel)
  const [filteredItems, setfilteredItems] = useState<any[] | undefined>(undefined)

  const dataProvider = useComboBoxDataProvider(async (page, nombre) => {
    return fetcher(page, nombre)
  })

  useEffect(() => {
    if (defaultLabel) {
      setfilteredItems([{ label: defaultLabel, value }])
    } else setfilteredItems(undefined)
  }, [defaultLabel, value])

  if (filteredItems) {
    return <ComboBox
      {...props}
      filteredItems={filteredItems}
      dataProvider={dataProvider}
      {...field(model)}
    />
  }

  return <ComboBox
    {...props}
    dataProvider={dataProvider}
    {...field(model)}
  />
}

interface ComboBoxFilterMultipleProps extends MultiSelectComboBoxProps<any> {
  fieldModel: ArrayModel
  defaultItems?: { label: string, value: number }[],
  fetcher: (pageable: Pageable, filter: string) => Promise<any[]>
}

export function ComboBoxFilterMultiple({
  fetcher, defaultItems, fieldModel, ...props
}: ComboBoxFilterMultipleProps) {
  const { value, errors, invalid, setValue } = useFormPart(fieldModel)

  const dataProvider = useComboBoxDataProvider(async (page, nombre) => {
    return fetcher(page, nombre)
  })

  const [selectedItems, setSelectedItems] = useState<any[]>(value ?? [])

  useEffect(() => {
    setSelectedItems(value ?? [])
  }, [value])

  const onSelectedItemsChanged = (e: MultiSelectComboBoxSelectedItemsChangedEvent<any>) => {
    const items = e.detail?.value ?? []
    setSelectedItems(items)
  }

  if (defaultItems && defaultItems.length > 0) {
    return (
      <MultiSelectComboBox
        {...props}
        filteredItems={defaultItems}
        dataProvider={dataProvider}
        selectedItems={selectedItems}
        onSelectedItemsChanged={onSelectedItemsChanged}
      />
    )
  }

  return <>
    <MultiSelectComboBox
      {...props}
      dataProvider={dataProvider}
      selectedItems={selectedItems}
      onBlur={() => {
        setValue(selectedItems)
      }}
      invalid={invalid}
      errorMessage={errors.map(item => item.message || item.validatorMessage).join('\n')}
      onSelectedItemsChanged={onSelectedItemsChanged}
    />
  </>
}