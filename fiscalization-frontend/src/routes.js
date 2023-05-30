export const baseUrl = 'http://127.0.0.1'

export const invoiceBaseUrl = baseUrl + '/api/v1/invoice'
export const invoiceAllUrl = invoiceBaseUrl + '/all'
export const invoiceUrl = (id) => invoiceBaseUrl + `/${id}`
export const invoiceStartFiscalizationUrl = (id) => invoiceUrl(id) + '/fiscalize'
export const invoiceGetItemsUrl = (id) => invoiceUrl(id) + '/items'

export const catalogBaseUrl = baseUrl + '/api/v1/catalog'
export const catalogAllUrl = catalogBaseUrl + '/all'
export const catalogUrl = (id) => catalogBaseUrl + `/${id}`
