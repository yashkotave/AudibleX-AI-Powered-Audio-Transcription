import { useState } from 'react'
import Icon from './Icon.jsx'

export default function TranscriptCard({ transcription, onDismiss }) {
  const [copied, setCopied] = useState(false)
  if (!transcription) return null

  async function copyTranscript() {
    try {
      await navigator.clipboard.writeText(transcription.transcript)
      setCopied(true)
      window.setTimeout(() => setCopied(false), 1800)
    } catch {
      setCopied(false)
    }
  }

  return (
    <section className="panel transcript-panel" aria-live="polite">
      <div className="transcript-heading">
        <div className="transcript-title">
          <span className="success-icon"><Icon name="check" size={16} /></span>
          <div><span className="eyebrow">TRANSCRIPTION COMPLETE</span><h2>{transcription.title}</h2></div>
        </div>
        <div className="transcript-actions">
          <button type="button" className="button-secondary" onClick={copyTranscript}><Icon name="copy" size={16} /> {copied ? 'Copied' : 'Copy text'}</button>
          <button type="button" className="icon-button" onClick={onDismiss} aria-label="Close transcript"><Icon name="close" size={17} /></button>
        </div>
      </div>
      <p className="transcript-body">{transcription.transcript}</p>
    </section>
  )
}
