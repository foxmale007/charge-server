(window.webpackJsonp=window.webpackJsonp||[]).push([["permission-page"],{"0aa9":function(e,t,n){"use strict";var r=n("7a23"),a={style:{"margin-bottom":"15px"}},c=Object(r.n)("p",null,"切换权限：",-1);var o=n("1da1"),u=(n("96cf"),n("0613")),i=n("d167"),l=Object(r.r)({name:"SwitchRoles",emits:["change"],setup:function(e,t){var n=t.emit,a=Object(u.b)(),c=Object(r.i)((function(){return a.state.user.roles})),l=Object(r.Q)(c.value[0]);return Object(r.hb)(l,function(){var e=Object(o.a)(regeneratorRuntime.mark((function e(t){return regeneratorRuntime.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,a.dispatch(i.a.ACTION_CHANGE_ROLES,t);case 2:n("change");case 3:case"end":return e.stop()}}),e)})));return function(t){return e.apply(this,arguments)}}()),{roles:c,currentRole:l}}});l.render=function(e,t,n,o,u,i){var l=Object(r.U)("el-radio-button"),s=Object(r.U)("el-radio-group");return Object(r.L)(),Object(r.m)("div",null,[Object(r.n)("div",a," 你的权限： "+Object(r.Y)(e.roles),1),c,Object(r.q)(s,{modelValue:e.currentRole,"onUpdate:modelValue":t[0]||(t[0]=function(t){return e.currentRole=t})},{default:Object(r.jb)((function(){return[Object(r.q)(l,{label:"editor"}),Object(r.q)(l,{label:"admin"})]})),_:1},8,["modelValue"])])},t.a=l},3252:function(e,t,n){"use strict";n.r(t);var r=n("7a23"),a={class:"app-container"};var c=n("6c02"),o=n("0aa9"),u=Object(r.r)({name:"PagePermission",components:{SwitchRoles:o.a},setup:function(){var e=Object(c.d)();return{handleRolesChange:function(){e.push({path:"/401"}).catch((function(e){console.warn(e)}))}}}});u.render=function(e,t,n,c,o,u){var i=Object(r.U)("SwitchRoles");return Object(r.L)(),Object(r.m)("div",a,[Object(r.q)(i,{onChange:e.handleRolesChange},null,8,["onChange"])])},t.default=u}}]);