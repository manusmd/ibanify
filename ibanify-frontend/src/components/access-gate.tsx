import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { LanguageSwitcher } from '@/components/language-switcher'
import {
  getAccessPasswordExpected,
  isAccessGranted,
  persistAccessGrant,
} from '@/lib/access-grant'

export function AccessGate({ children }: { children: React.ReactNode }) {
  const { t, i18n } = useTranslation()
  const expected = getAccessPasswordExpected()
  const [unlocked, setUnlocked] = useState(!expected)
  const [checking, setChecking] = useState(!!expected)
  const [password, setPassword] = useState('')
  const [wrong, setWrong] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    document.title = t('app.documentTitle')
    document.documentElement.lang = i18n.language.startsWith('de') ? 'de' : 'en'
  }, [t, i18n.language])

  useEffect(() => {
    if (!expected) {
      return
    }
    let cancelled = false
    void (async () => {
      const ok = await isAccessGranted(expected)
      if (!cancelled && ok) {
        setUnlocked(true)
      }
      if (!cancelled) {
        setChecking(false)
      }
    })()
    return () => {
      cancelled = true
    }
  }, [expected])

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!expected || submitting) {
      return
    }
    setSubmitting(true)
    setWrong(false)
    if (password === expected) {
      await persistAccessGrant(expected)
      setUnlocked(true)
      setPassword('')
    } else {
      setWrong(true)
    }
    setSubmitting(false)
  }

  if (checking) {
    return (
      <div className="relative min-h-svh overflow-hidden">
        <div
          aria-hidden
          className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_90%_60%_at_50%_-18%,oklch(0.34_0.09_285/0.42),transparent_52%)]"
        />
        <div className="text-muted-foreground relative z-10 flex min-h-svh items-center justify-center text-sm">
          {t('access.checking')}
        </div>
      </div>
    )
  }

  if (!unlocked) {
    return (
      <div className="relative min-h-svh overflow-hidden">
        <div
          aria-hidden
          className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_90%_60%_at_50%_-18%,oklch(0.34_0.09_285/0.42),transparent_52%)]"
        />
        <div className="relative z-10 flex min-h-svh flex-col items-center px-3 pb-6 pt-6 sm:px-4 sm:pb-8 sm:pt-8">
          <header className="mb-4 flex w-full max-w-md justify-end sm:mb-5">
            <LanguageSwitcher />
          </header>
          <main className="w-full max-w-md">
            <div className="rounded-xl border border-border/80 bg-card/55 p-4 shadow-2xl shadow-black/30 ring-1 ring-white/5 backdrop-blur-md sm:p-5">
              <h1 className="text-foreground mb-1 text-center text-lg font-semibold tracking-tight">
                {t('access.title')}
              </h1>
              <p className="text-muted-foreground mb-4 text-center text-xs leading-snug">
                {t('access.description')}
              </p>
              <form onSubmit={onSubmit} className="flex flex-col gap-3">
                <div className="space-y-1">
                  <Label htmlFor="access-password" className="text-xs font-medium">
                    {t('access.passwordLabel')}
                  </Label>
                  <Input
                    id="access-password"
                    type="password"
                    name="password"
                    autoComplete="current-password"
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value)
                      setWrong(false)
                    }}
                    disabled={submitting}
                    aria-invalid={wrong}
                    className="h-10 rounded-lg border-border/80 bg-background/50 text-sm"
                  />
                  {wrong && (
                    <p className="text-destructive text-xs leading-snug" role="alert">
                      {t('access.error')}
                    </p>
                  )}
                </div>
                <Button
                  type="submit"
                  className="h-10 w-full rounded-lg text-sm font-medium"
                  disabled={submitting}
                >
                  {t('access.submit')}
                </Button>
              </form>
            </div>
          </main>
        </div>
      </div>
    )
  }

  return <>{children}</>
}
