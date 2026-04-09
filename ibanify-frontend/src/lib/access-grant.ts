const STORAGE_KEY = 'ibanify.access.sha256'

export function getAccessPasswordExpected(): string {
  return (import.meta.env.VITE_ACCESS_PASSWORD ?? '').trim()
}

export async function sha256Hex(text: string): Promise<string> {
  const buf = new TextEncoder().encode(text)
  const hash = await crypto.subtle.digest('SHA-256', buf)
  return Array.from(new Uint8Array(hash))
    .map((b) => b.toString(16).padStart(2, '0'))
    .join('')
}

export async function isAccessGranted(expected: string): Promise<boolean> {
  if (!expected) {
    return true
  }
  const want = await sha256Hex(expected)
  return localStorage.getItem(STORAGE_KEY) === want
}

export async function persistAccessGrant(expected: string): Promise<void> {
  const want = await sha256Hex(expected)
  localStorage.setItem(STORAGE_KEY, want)
}
