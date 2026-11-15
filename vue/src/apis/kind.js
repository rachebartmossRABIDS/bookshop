import instance from '../utils/request'
export function kindListService(){
   return instance.get('/kind/list');
}