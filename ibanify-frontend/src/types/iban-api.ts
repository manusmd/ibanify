export type ValidateIbanRequest = {
  input: string
}

export type ValidateIbanResponse = {
  normalizedIban: string
  displayIban: string
  valid: boolean
  bankName: string | null
  bic: string | null
  bankCode: string | null
  bankCity: string | null
  bankZip: string | null
  bankLogoUrl: string | null
  messages: string[]
}

export type ApiErrorResponse = {
  code: string
  message: string
}
