import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { IbanValidateForm } from '@/components/iban/iban-validate-form'
import { LanguageSwitcher } from '@/components/language-switcher'

function App() {
  const { t, i18n } = useTranslation()

  useEffect(() => {
    document.title = t('app.documentTitle')
    document.documentElement.lang = i18n.language.startsWith('de') ? 'de' : 'en'
  }, [t, i18n.language])

  return (
    <div className="relative min-h-svh overflow-hidden">
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_90%_60%_at_50%_-18%,oklch(0.34_0.09_285/0.42),transparent_52%)]"
      />
      <div className="relative z-10 flex min-h-svh flex-col items-center px-3 pb-6 pt-6 sm:px-4 sm:pb-8 sm:pt-8">
        <header className="mb-4 flex w-full max-w-2xl flex-col gap-2 sm:mb-5">
          <div className="flex justify-end">
            <LanguageSwitcher />
          </div>
          <h1 className="text-foreground text-center text-2xl font-semibold tracking-tight sm:text-3xl">
            {t('app.title')}
          </h1>
          <p className="text-muted-foreground text-pretty text-center text-xs leading-snug sm:text-sm">
            {t('app.tagline')}
          </p>
        </header>
        <main className="w-full max-w-2xl">
          <div className="rounded-xl border border-border/80 bg-card/55 p-4 shadow-2xl shadow-black/30 ring-1 ring-white/5 backdrop-blur-md sm:p-5">
            <IbanValidateForm />
          </div>
        </main>
      </div>
    </div>
  )
}

export default App
