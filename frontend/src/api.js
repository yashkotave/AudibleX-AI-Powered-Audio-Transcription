const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api').replace(/\/$/, '')

async function request(path, options = {}) {
  let response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, options)
  } catch {
    throw new Error('Unable to connect to AudibleX. Check that the backend is running.')
  }

  if (!response.ok) {
    const body = await response.json().catch(() => null)
    throw new Error(body?.error || body?.message || `Request failed (${response.status}).`)
  }

  if (response.status === 204) return null
  return response.json()
}

export function listTranscriptions() {
  return request('/transcriptions')
}

export function searchTranscriptions(keyword) {
  return request(`/transcriptions/search?keyword=${encodeURIComponent(keyword)}`)
}

export function getTranscription(id) {
  return request(`/transcriptions/${id}`)
}

export function createTranscription(file) {
  const data = new FormData()
  data.append('file', file)
  return request('/transcriptions', { method: 'POST', body: data })
}

export function updateTranscription(id, updates) {
  return request(`/transcriptions/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updates),
  })
}

export function deleteTranscription(id) {
  return request(`/transcriptions/${id}`, { method: 'DELETE' })
}
