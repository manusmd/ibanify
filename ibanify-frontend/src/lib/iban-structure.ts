export type IbanStructureSegmentKind =
  | 'iban'
  | 'country'
  | 'check'
  | 'bankCode'
  | 'account'
  | 'bban'

export type IbanStructureSegment = {
  kind: IbanStructureSegmentKind
  value: string
}

export function getIbanStructureSegments(
  normalizedIban: string,
): IbanStructureSegment[] {
  const u = normalizedIban.replace(/[^A-Za-z0-9]/g, '').toUpperCase()
  if (u.length < 4) {
    return [{ kind: 'iban', value: u }]
  }
  const country = u.slice(0, 2)
  const check = u.slice(2, 4)
  const bban = u.slice(4)

  if (country === 'DE' && u.length === 22 && /^\d{18}$/.test(bban)) {
    return [
      { kind: 'country', value: country },
      { kind: 'check', value: check },
      { kind: 'bankCode', value: bban.slice(0, 8) },
      { kind: 'account', value: bban.slice(8, 18) },
    ]
  }

  return [
    { kind: 'country', value: country },
    { kind: 'check', value: check },
    { kind: 'bban', value: bban },
  ]
}
