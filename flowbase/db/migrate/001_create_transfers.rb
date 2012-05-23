class CreateTransfers < ActiveRecord::Migration
  def self.up
    create_table :transfers do |t|
      t.column "agent_name",      :string
      t.column "agent_id",        :integer
      t.column "recipient_name",  :string
      t.column "recipient_id",    :integer
      t.column "value",           :float
      t.column "datetime",        :integer
      t.column "unit",            :string
    end
  end

  def self.down
    drop_table :transfers
  end
end
