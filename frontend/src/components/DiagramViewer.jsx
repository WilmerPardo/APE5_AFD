import { Image, RefreshCw } from 'lucide-react';
import { useEffect, useState } from 'react';

const imageNames = {
  telemetria: 'telemetria-afd-minimizado.png',
  bioinformatica: 'bioinformatica-afd-minimizado.png',
  ecommerce: 'ecommerce-afd-minimizado.png'
};

export default function DiagramViewer({ type }) {
  const [available, setAvailable] = useState(true);
  const [cacheKey, setCacheKey] = useState(() => Date.now());
  // El parametro evita que el navegador conserve una imagen anterior en cache.
  const src = `/images/${imageNames[type]}?v=${cacheKey}`;

  useEffect(() => {
    setAvailable(true);
    setCacheKey(Date.now());
  }, [type]);

  function refreshDiagram() {
    setAvailable(true);
    setCacheKey(Date.now());
  }

  return (
    <section className="panel diagram-panel">
      <div className="section-title diagram-title">
        <div>
          <Image size={20} aria-hidden="true" />
          <h2>Diagrama minimizado</h2>
        </div>
        <button className="diagram-refresh" type="button" title="Recargar diagrama" onClick={refreshDiagram}>
          <RefreshCw size={17} aria-hidden="true" />
        </button>
      </div>

      {available ? (
        <img
          src={src}
          alt={`AFD minimizado de ${type}`}
          onError={() => setAvailable(false)}
        />
      ) : (
        <div className="diagram-fallback">Diagrama no disponible</div>
      )}
    </section>
  );
}
