import instance from '../utils/request'

export const adminStatsService = () => instance.get('/admin/stats')
export const adminLogsService = params => instance.get('/admin/logs', { params })
