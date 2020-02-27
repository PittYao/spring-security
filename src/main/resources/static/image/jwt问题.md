1. 续签问题，传统的 cookie+session 的方案天然的支持续签，但是 jwt 由于服务端不保存用户状态，因此很难完美解决续签问题，如果引入 redis，虽然可以解决问题，但是 jwt 也变得不伦不类了。
2. 注销问题，由于服务端不再保存用户信息，所以一般可以通过修改 secret 来实现注销，服务端 secret 修改后，已经颁发的未过期的 token 就会认证失败，进而实现注销，不过毕竟没有传统的注销方便。
3. 密码重置，密码重置后，原本的 token 依然可以访问系统，这时候也需要强制修改 secret。
4. 基于第 2 点和第 3 点，一般建议不同用户取不同 secret。


### 现阶段采取jwt策略 

1. 在登录接口中 如果校验账号密码成功 则根据用户id和用户类型创建jwt token(有效期设置为-1，即永不过期),得到A
2. 更新登录日期(当前时间new Date()即可)（业务上可选），得到B
3. 在redis中缓存key为ACCESS_TOKEN:userId:A(加上A是为了防止用户多个客户端登录 造成token覆盖),value为B的毫秒数（转换成字符串类型），过期时间为7天（7 * 24 * 60 * 60）
4. 登录返回头中获取token
5. 用户在接口请求header中携带token进行登录，后端在所有接口前置拦截器进行拦截，作用是解析token 拿到userId和用户类型（用户调用业务接口只需要传token即可）， 如果解析失败（抛出SignatureException），则返回json（code = 0 ,info= Token验证不通过, errorCode = '1001'）； 此外如果解析成功，验证redis中key为ACCESS_TOKEN:userId:A 是否存在 如果不存在 则返回json（code = 0 ,info= 会话过期请重新登录, errorCode = '1002'）； 如果缓存key存在，则自动续7天超时时间（value不变），实现频繁登录用户免登陆。
6. 如果是修改密码或退出登录 则废除access_tokens（删除key）