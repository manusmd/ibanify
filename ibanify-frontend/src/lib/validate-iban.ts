import { getApiBaseUrl } from '@/lib/env'
import type {
  ApiErrorResponse,
  ValidateIbanRequest,
  ValidateIbanResponse,
} from '@/types/iban-api'

export type ValidateIbanClientErrorCode = 'network' | 'http_fallback'

export class ValidateIbanClientError extends Error {
  readonly status: number
  readonly code?: string
  readonly apiBody?: ApiErrorResponse
  readonly clientCode?: ValidateIbanClientErrorCode

  constructor(
    status: number,
    message: string,
    options?: {
      code?: string
      apiBody?: ApiErrorResponse
      cause?: unknown
      clientCode?: ValidateIbanClientErrorCode
    },
  ) {
    super(message, { cause: options?.cause })
    this.name = 'ValidateIbanClientError'
    this.status = status
    this.code = options?.code
    this.apiBody = options?.apiBody
    this.clientCode = options?.clientCode
  }
}

async function readApiError(res: Response): Promise<ApiErrorResponse | null> {
  const text = await res.text()
  if (!text) {
    return null
  }
  try {
    return JSON.parse(text) as ApiErrorResponse
  } catch {
    return null
  }
}

export async function validateIban(
  input: string,
  options?: { acceptLanguage?: string },
): Promise<ValidateIbanResponse> {
  const base = getApiBaseUrl()
  const url = `${base}/api/v1/iban/validate`
  const body: ValidateIbanRequest = { input }
  const acceptLanguage = options?.acceptLanguage?.trim()
  let res: Response
  try {
    res = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(acceptLanguage != null && acceptLanguage !== ''
          ? { 'Accept-Language': acceptLanguage }
          : {}),
      },
      body: JSON.stringify(body),
    })
  } catch (cause) {
    throw new ValidateIbanClientError(0, '', { cause, clientCode: 'network' })
  }
  if (!res.ok) {
    const apiBody = await readApiError(res)
    const msg = apiBody?.message?.trim()
    if (msg && apiBody) {
      throw new ValidateIbanClientError(res.status, msg, {
        code: apiBody.code,
        apiBody,
      })
    }
    throw new ValidateIbanClientError(res.status, '', {
      code: apiBody?.code,
      apiBody: apiBody ?? undefined,
      clientCode: 'http_fallback',
    })
  }
  const raw = (await res.json()) as ValidateIbanResponse
  return {
    ...raw,
    messages: raw.messages ?? [],
    bankLogoUrl: raw.bankLogoUrl ?? null,
  }
}
