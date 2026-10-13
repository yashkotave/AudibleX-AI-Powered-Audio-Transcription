import { useRef, useState } from 'react'
import Icon from './Icon.jsx'

const ACCEPTED = '.mp3,.mp4,.mpeg,.mpga,.m4a,.wav,.webm'
const MAX_BYTES = 25 * 1024 * 1024

function formatSize(bytes) {
  return bytes < 1024 * 1024
    ? `${Math.max(1, Math.round(bytes / 1024))} KB`
    : `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

export default function UploadCard({ onTranscribe, busy }) {
  const inputRef = useRef(null)
  const [file, setFile] = useState(null)
  const [dragging, setDragging] = useState(false)
  const [error, setError] = useState('')

  function chooseFile(nextFile) {
    setError('')
    if (!nextFile) return
    const ext = nextFile.name.split('.').pop()?.toLowerCase()
    if (!['mp3', 'mp4', 'mpeg', 'mpga', 'm4a', 'wav', 'webm'].includes(ext)) {
      setFile(null)
      setError('That audio format is not supported. Choose MP3, MP4, MPEG, MPGA, M4A, WAV, or WEBM.')
      return
    }
    if (nextFile.size > MAX_BYTES) {
      setFile(null)
      setError('The audio file must be 25 MB or smaller.')
      return
    }
    setFile(nextFile)
  }

  async function submit() {
    if (!file || busy) return
    try {
      await onTranscribe(file)
      setFile(null)
      if (inputRef.current) inputRef.current.value = ''
    } catch {
      // The parent displays the API error and keeps the selected file available for retry.
    }
  }

  return (
    <section className="panel upload-panel">
      <div className="panel-heading">
        <div>
          <span className="eyebrow">NEW TRANSCRIPTION</span>
          <h2>Bring your audio to life</h2>
          <p>Upload a recording and let AI do the listening.</p>
        </div>
        <div className="panel-step">01 <span>/ 01</span></div>
      </div>
      <button type="button" className={`dropzone ${dragging ? 'dragging' : ''}`}
        onClick={() => inputRef.current?.click()}
        onDragOver={(event) => { event.preventDefault(); setDragging(true) }}
        onDragLeave={() => setDragging(false)}
        onDrop={(event) => { event.preventDefault(); setDragging(false); chooseFile(event.dataTransfer.files?.[0]) }}>
        <span className="upload-icon"><Icon name="upload" size={21} /></span>
        <strong>Drop your audio file here</strong>
        <span className="dropzone-sub">or <span className="browse-link">browse files</span> from your device</span>
        <span className="format-label">MP3 · MP4 · WAV · M4A · WEBM <i /> UP TO 25 MB</span>
      </button>
      <input ref={inputRef} className="visually-hidden" type="file" accept={ACCEPTED}
        onChange={(event) => chooseFile(event.target.files?.[0])} />
      {file && (
        <div className="selected-file">
          <span className="file-icon"><Icon name="file" size={19} /></span>
          <span className="selected-file-info"><strong>{file.name}</strong><span>{formatSize(file.size)} · Ready to transcribe</span></span>
          <button className="icon-button" type="button" aria-label="Remove selected file"
            onClick={() => { setFile(null); if (inputRef.current) inputRef.current.value = '' }}>
            <Icon name="close" size={17} />
          </button>
        </div>
      )}
      {error && <p className="inline-error" role="alert">{error}</p>}
      <div className="upload-footer">
        <span className="privacy-note"><span className="privacy-dot" /> Your audio is securely processed</span>
        <button type="button" className="button-primary" disabled={!file || busy} onClick={submit}>
          {busy ? <><span className="spinner" /> Transcribing…</> : <>Transcribe audio <Icon name="chevron" size={17} /></>}
        </button>
      </div>
    </section>
  )
}
