export type ExampleIban = {
  iban: string
  nameKey: string
}

export const EXAMPLE_IBANS: ExampleIban[] = [
  { iban: 'DE02300209000106531065', nameKey: 'form.examples.targobank' },
  { iban: 'DE02200505501015871393', nameKey: 'form.examples.haspa' },
  { iban: 'DE02120300000000202051', nameKey: 'form.examples.dkbb' },
  { iban: 'DE02500105170137075030', nameKey: 'form.examples.ing' },
  { iban: 'DE02100500000054540402', nameKey: 'form.examples.lbb' },
]

export function formatIbanForInput(iban: string): string {
  const compact = iban.replace(/\s/g, '')
  return compact.replace(/(.{4})/g, '$1 ').trim()
}
