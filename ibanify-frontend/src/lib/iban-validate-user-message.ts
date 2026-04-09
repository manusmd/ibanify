import type { TFunction } from 'i18next'
import type { ValidateIbanClientError } from '@/lib/validate-iban'

export function userFacingValidateIbanError(
  err: ValidateIbanClientError,
  t: TFunction,
): string {
  if (err.clientCode === 'network') {
    return t('errors.network')
  }
  const fromApi = err.apiBody?.message?.trim()
  if (fromApi) {
    return fromApi
  }
  if (err.clientCode === 'http_fallback') {
    return t('errors.httpStatus', { status: err.status })
  }
  return t('errors.generic')
}
