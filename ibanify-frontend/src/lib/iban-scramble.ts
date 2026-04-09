const CHARSET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
const LETTERS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'

export function randomCharMatching(target: string, index: number): string {
  const c = target[index]
  if (c === undefined) {
    return 'X'
  }
  if (c >= '0' && c <= '9') {
    return String(Math.floor(Math.random() * 10))
  }
  if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
    const ch = LETTERS[Math.floor(Math.random() * LETTERS.length)]
    return c >= 'a' && c <= 'z' ? (ch?.toLowerCase() ?? 'x') : (ch ?? 'X')
  }
  if (c === ' ') {
    return ' '
  }
  return c
}

export function randomIbanLikeString(length: number): string {
  let s = ''
  for (let i = 0; i < length; i++) {
    const c = CHARSET[Math.floor(Math.random() * CHARSET.length)]
    s += c ?? 'X'
  }
  return s
}

export function stripIbanToAlnum(value: string): string {
  return value.replace(/[^A-Za-z0-9]/g, '')
}
