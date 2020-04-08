set schema 'y2020';

alter table sale add column pending_approval boolean;

update sale set pending_approval = false;