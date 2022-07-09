create table zy_timeline(
    title varchar2(50) ,
    context varchar2(500)
);
COMMENT ON table zy_timeline IS '事件线';
comment on column zy_timeline.title is '标题';
comment on column zy_timeline.context is '详情';