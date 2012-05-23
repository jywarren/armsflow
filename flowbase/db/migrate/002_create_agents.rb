class CreateAgents < ActiveRecord::Migration
  def self.up
    create_table :agents do |t|
      t.column "name",          :string
      t.column "lat",           :float
      t.column "lon",           :float
    end
  end

  def self.down
    drop_table :agents
  end
end
