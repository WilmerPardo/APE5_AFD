const jsonHeaders = {
  'Content-Type': 'application/json'
};

async function parseResponse(response) {
  // Centraliza errores HTTP para que la interfaz maneje un solo tipo de fallo.
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Error HTTP ${response.status}`);
  }
  return response.json();
}

export async function getAutomata(type) {
  const response = await fetch(`/api/automatas/${type}`);
  return parseResponse(response);
}

export async function validateChain(type, cadena) {
  const response = await fetch(`/api/automatas/${type}/validar`, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify({ cadena })
  });
  return parseResponse(response);
}
