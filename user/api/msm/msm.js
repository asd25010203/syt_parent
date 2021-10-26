import request from '@/utils/request'

const api_name = `/admin/mail`

export default {
  sendCode(mobile) {
    return request({
      url: `${api_name}/loginMail/${mobile}`,
      method: `get`
    })
  }
}
